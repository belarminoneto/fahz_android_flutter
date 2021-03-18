package br.com.avanade.fahz.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.AddAnnualRenewalDocumentsActivity;
import br.com.avanade.fahz.activities.DocumentsActivity;
import br.com.avanade.fahz.activities.benefits.scholarship.BaseScholarshipControlActivity;
import br.com.avanade.fahz.activities.benefits.schoolsupplies.BaseSchoolSuppliesControlActivity;
import br.com.avanade.fahz.activities.benefits.toy.RequestNewToyActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.Document;
import br.com.avanade.fahz.model.DocumentType;
import br.com.avanade.fahz.model.NotSupportedSource;
import br.com.avanade.fahz.model.ProfileImage;
import br.com.avanade.fahz.model.UploadResponse;
import br.com.avanade.fahz.model.document.DocumentTypeBody;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.DocumentsHelper;
import br.com.avanade.fahz.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static br.com.avanade.fahz.util.Constants.GALLERY_INTENT_CALLED;
import static br.com.avanade.fahz.util.Constants.GALLERY_KITKAT_INTENT_CALLED;
import static br.com.avanade.fahz.util.Constants.MAX_IMAGE_SIZE;
import static br.com.avanade.fahz.util.Constants.REQUEST_CAMERA_CAPTURE;
import static br.com.avanade.fahz.util.Constants.REQUEST_CAMERA_PERMISSION;
import static br.com.avanade.fahz.util.Constants.REQUEST_PICTURE_GALLERY;
import static br.com.avanade.fahz.util.Constants.REQUEST_STORAGE_PERMISSION;
import static br.com.avanade.fahz.util.Constants.TYPE_FAILURE;

public class UploadDialogFragment extends DialogFragment
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "UploadFragment";
    private static final String DIALOG_FRAGMENT = "dialog";

    private String mCpf;
    private String mId;
    private boolean mNewType;
    private File mFile;
    private AlertDialog mAlertDialog;
    private OnChangePhotoListener mOnChangePhotoListener;
    private Context mContext;
    DocumentType type = null;
    Uri imageUri;

    private List<DocumentType> typeDocuments = new ArrayList<>();
    private List<String> typeDocumentsStr = new ArrayList<>();

    @BindView(R.id.newTypeContainer)
    RelativeLayout newTypeContainer;
    @BindView(R.id.separatorNewType)
    View separatorNewType;
    @BindView(R.id.typedocument_spinner)
    Spinner typeDocumentSpinner;

    public static String getRealPathFromURI(Uri contentURI, Context context) {
        String path = contentURI.getPath();
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = context.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            assert cursor != null;
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        } catch (Exception e) {
            return path;
        }
        return path;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap rotateImage(String filePath, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (rotation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mOnChangePhotoListener = (OnChangePhotoListener) getContext();
        mContext = getContext();

        String title = "";
        if (getArguments() != null) {
            title = getArguments().getString("title");
            mCpf = getArguments().getString("cpf");
            mId = String.valueOf(getArguments().getInt("id"));
            mNewType = getArguments().getBoolean("isNewType");
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_upload, null);
        ButterKnife.bind(this, view);

        RelativeLayout viewGallery = view.findViewById(R.id.viewGallery);
        RelativeLayout viewCamera = view.findViewById(R.id.viewCamera);

        viewGallery.setOnClickListener(v -> {
            if (mNewType && typeDocumentSpinner.getSelectedItemPosition() == 0)
                ((DocumentsActivity) mContext).showSnackBar("É necessáio escolher o tipo de documento para inclusão.", TYPE_FAILURE);
            else
                getPicture();
        });

        viewCamera.setOnClickListener(v -> {
            if (mNewType && typeDocumentSpinner.getSelectedItemPosition() == 0)
                ((DocumentsActivity) mContext).showSnackBar("É necessáio escolher o tipo de documento para inclusão", TYPE_FAILURE);
            else
                takePhoto();
        });

        if (!mNewType) {
            newTypeContainer.setVisibility(View.GONE);
            separatorNewType.setVisibility(View.GONE);
        } else {
            newTypeContainer.setVisibility(View.VISIBLE);
            separatorNewType.setVisibility(View.VISIBLE);
        }

        populateListTypeDocument();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setView(view);
        mAlertDialog = builder.create();

        return mAlertDialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Context context = FahzApplication.getAppContext();
        if (!isDeviceSupportCamera()) {
            Toast.makeText(context, getString(R.string.device_not_support_camera), Toast.LENGTH_LONG).show();
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION);
        }

        mFile = new File(context.getExternalFilesDir(null), "picture.jpg");
        super.onActivityCreated(savedInstanceState);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION || requestCode == REQUEST_PICTURE_GALLERY || requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                if (requestCode == REQUEST_CAMERA_PERMISSION)
                    ErrorDialog.newInstance(getString(R.string.request_permission))
                            .show(getChildFragmentManager(), DIALOG_FRAGMENT);
                else if (requestCode == REQUEST_STORAGE_PERMISSION)
                    ErrorDialog.newInstance(getString(R.string.request_permission_storage))
                            .show(getChildFragmentManager(), DIALOG_FRAGMENT);

            } else {
                callExternaStorage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void callExternaStorage() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION);
        } else if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION);
        }
    }

    //CHAMADA AO ENDPOINT QUE MOSTRA OS DOCUMENTOS QUE O USUARIO NÂO POSSUI
    private void populateListTypeDocument() {
        APIService mAPIService = ServiceGenerator.createService(APIService.class, null, null);
        Call<Document> call = mAPIService.queryDocumentType(new DocumentTypeBody(mCpf, true));
        call.enqueue(new Callback<Document>() {
            @Override
            public void onResponse(@NonNull Call<Document> call, @NonNull Response<Document> response) {
                typeDocumentsStr.add("Selecione");
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    Document document = response.body();
                    assert document != null;
                    List<DocumentType> listDocumentTypes = document.documentTypes;
                    for (DocumentType documentType : listDocumentTypes) {
                        typeDocuments.add(new DocumentType(documentType.id,
                                documentType.description,
                                documentType.documents,
                                documentType.userHasIt));

                        typeDocumentsStr.add(documentType.description);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                            R.layout.spinner_layout, typeDocumentsStr);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    typeDocumentSpinner.setAdapter(adapter);
                    typeDocumentSpinner.setSelection(0);

                } else{
                    if(mContext instanceof DocumentsActivity)
                        ((DocumentsActivity) mContext).showSnackBar(getString(R.string.problemServer), TYPE_FAILURE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Document> call, @NonNull Throwable t) {
                if(mContext instanceof DocumentsActivity) {
                    if(getActivity()!= null) {
                        if (t instanceof SocketTimeoutException)
                            ((DocumentsActivity) mContext).showSnackBar(getResources().getString(getResources().getIdentifier("MSG362", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else if (t instanceof UnknownHostException)
                            ((DocumentsActivity) mContext).showSnackBar(getResources().getString(getResources().getIdentifier("MSG361", "string", getActivity().getPackageName())), TYPE_FAILURE);
                        else
                            ((DocumentsActivity) mContext).showSnackBar(t.getMessage(), TYPE_FAILURE);
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermissionType(String permission, int request_code) {
        if (permission.equals(Manifest.permission.CAMERA)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE) || permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PICTURE_GALLERY);
        }

        if (shouldShowRequestPermissionRationale(permission)) {
            requestPermissions(new String[]{permission}, request_code);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION);
        } else {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            PackageManager pm = FahzApplication.getAppContext().getPackageManager();
            if (takePhotoIntent.resolveActivity(pm) != null) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = Objects.requireNonNull(getActivity()).getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePhotoIntent, REQUEST_CAMERA_CAPTURE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getPicture() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionType(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION);
        } else {

            assert getTag() != null;
            if (getTag().equals("changePhoto")) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_PICTURE_GALLERY);
            } else {
                String[] mimeTypes =
                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/pdf",
                                "image/*"};

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mAlertDialog.dismiss();
            if (requestCode == REQUEST_CAMERA_CAPTURE) {
                try {
                    File file = createImageFile();
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(
                            FahzApplication.getAppContext().getContentResolver(), imageUri);

                    imageBitmap = rotateImage(getRealPathFromURI(imageUri,getContext()),imageBitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    if (imageBitmap != null) {
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        byte[] imageInByte = stream.toByteArray();
                        long lengthbmp = imageInByte.length;
                        long length = (lengthbmp / 1024) / 1024;
                        if (length > MAX_IMAGE_SIZE) {
                            int resID = getResources().getIdentifier("MSG022", "string", mContext.getPackageName());
                            String message = getResources().getString(resID);
                            assert getTag() != null;
                            if (getTag().equals("changePhoto")) {
                                mOnChangePhotoListener.onChangePhoto(message);
                            } else {
                                Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                            }
                        } else {
                            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                                    FahzApplication.getAppContext().getContentResolver(), imageUri);

                            thumbnail = rotateImage(getRealPathFromURI(imageUri,getContext()),thumbnail);
                            mFile = resizeAndCompressFile(thumbnail, file.getAbsolutePath());

                            if (getTag() != null) {
                                if (getTag().equals("changePhoto")) {
                                    changePhoto();
                                } else if (getTag().equals("uploadFile")) {
                                    uploadFile();
                                }
                            }
                        }
                    } else {
                        Utils.showSimpleDialog(getString(R.string.dialog_title), "Favor tirar uma nova foto.", null, getActivity(), null);
                    }
                } catch (IOException e) {
                    LogUtils.error(TAG, e);
                }

            } else if (requestCode == GALLERY_INTENT_CALLED || requestCode == GALLERY_KITKAT_INTENT_CALLED || requestCode == REQUEST_PICTURE_GALLERY ) {
                if (null == data) return;
                Uri originalUri = data.getData();

                try {
                    Bitmap bitmapFile = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ImageDecoder.Source decoder = null;
                        decoder = ImageDecoder.createSource(getFileFromGallery(originalUri));
                        String mimeType = FahzApplication.getAppContext().getContentResolver().getType(originalUri);
                        if (mimeType.toUpperCase().equalsIgnoreCase("IMAGE/PNG")
                                || mimeType.toUpperCase().equalsIgnoreCase("IMAGE/JPEG")
                                || mimeType.toUpperCase().equalsIgnoreCase("IMAGE/WEBP")
                                || mimeType.toUpperCase().equalsIgnoreCase("IMAGE/GIF")
                                || mimeType.toUpperCase().equalsIgnoreCase("IMAGE/HEIF")) {
                            bitmapFile = ImageDecoder.decodeBitmap(decoder);
                        }
                    }
                    else {
                        bitmapFile = MediaStore.Images.Media.getBitmap(FahzApplication.getAppContext().getContentResolver(), originalUri);
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    if (bitmapFile != null) {
                        if (!stream.equals("")) {
                            String fileName;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                fileName = getFileFromGallery(originalUri).getAbsolutePath();
                            }
                            else {
                                fileName = getRealPathFromURI(originalUri);
                            }
                            bitmapFile.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                            byte[] imageInByte = stream.toByteArray();
                            long lengthbmp = imageInByte.length;
                            long length = (lengthbmp / 1024) / 1024;

                            if (length > MAX_IMAGE_SIZE) {
                                int resID = getResources().getIdentifier("MSG022", "string", mContext.getPackageName());
                                String message = getResources().getString(resID);
                                assert getTag() != null;
                                if (getTag().equals("changePhoto")) {
                                    mOnChangePhotoListener.onChangePhoto(message);
                                } else {
                                    Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                                }
                            } else {
                                assert originalUri != null;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    mFile = getFileFromGallery(originalUri);
                                } else {
                                    bitmapFile = rotateImage(getRealPathFromURI(originalUri, getContext()), bitmapFile);
                                }

                                if (fileName == null || fileName.equals("")) {
                                    if (DocumentsHelper.isGooglePhotosUriDrive(originalUri)) {
                                        mFile = DocumentsHelper.saveFileIntoExternalStorageByUri(Objects.requireNonNull(getActivity()), originalUri);
                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            mFile = getFileFromGallery(originalUri);
                                        } else {

                                            String pathTemp = br.com.avanade.fahz.util.FileUtils.getPath(mContext, originalUri);
                                            final File file = new File(pathTemp);
                                            String path = file.getPath();
                                            assert path != null;
                                            mFile = new File(path);
                                        }
                                    }
                                } else
                                    mFile = resizeAndCompressFile(bitmapFile, fileName);

                                if (getTag() != null) {
                                    if (getTag().equals("changePhoto")) {
                                        changePhoto();
                                    } else if (getTag().equals("uploadFile")) {
                                        uploadFile();
                                    }
                                }
                            }
                        } else {
                            assert originalUri != null;
                            if (DocumentsHelper.isGooglePhotosUriDrive(originalUri)) {
                                mFile = DocumentsHelper.saveFileIntoExternalStorageByUri(Objects.requireNonNull(getActivity()), originalUri);
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    mFile = getFileFromGallery(originalUri);
                                }
                                else {
                                    String path = DocumentsHelper.getFilePath(getActivity(), originalUri);
                                    assert path != null;
                                    mFile = new File(path);
                                }
                            }

                            int file_size = Integer.parseInt(String.valueOf((mFile.length() / 1024) / 1024));
                            if (file_size > MAX_IMAGE_SIZE) {
                                int resID = getResources().getIdentifier("MSG022", "string", mContext.getPackageName());
                                String message = getResources().getString(resID);
                                assert getTag() != null;
                                if (getTag().equals("changePhoto")) {
                                    mOnChangePhotoListener.onChangePhoto(message);
                                } else {
                                    Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                                }
                            } else {

                                if (getTag() != null) {
                                    if (getTag().equals("uploadFile")) {
                                        uploadFile();
                                    }
                                }
                            }
                        }
                    } else {
                        assert originalUri != null;
                        if (DocumentsHelper.isGooglePhotosUriDrive(originalUri)) {
                            mFile = DocumentsHelper.saveFileIntoExternalStorageByUri(Objects.requireNonNull(getActivity()), originalUri);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                mFile = getFileFromGallery(originalUri);
                            } else {

                                String pathTemp = br.com.avanade.fahz.util.FileUtils.getPath(mContext, originalUri);
                                final File file = new File(pathTemp);
                                String path = file.getPath();

                                assert path != null;
                                mFile = new File(path);
                            }
                        }

                        int file_size = Integer.parseInt(String.valueOf((mFile.length() / 1024) / 1024));
                        if (file_size > MAX_IMAGE_SIZE) {
                            int resID = getResources().getIdentifier("MSG022", "string", mContext.getPackageName());
                            String message = getResources().getString(resID);
                            assert getTag() != null;
                            if (getTag().equals("changePhoto")) {
                                mOnChangePhotoListener.onChangePhoto(message);
                            } else {
                                Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
                            }
                        } else {

                            if (getTag() != null) {
                                if (getTag().equals("uploadFile")) {
                                    uploadFile();
                                }
                            }
                        }
                    }
                } catch (IOException | URISyntaxException e) {
                    LogUtils.error(TAG, e);
                } catch (NotSupportedSource notSupportedSource) {
                    assert getTag() != null;
                    if (getTag().equals("changePhoto")) {
                        mOnChangePhotoListener.onChangePhoto(notSupportedSource.getMessage());
                    } else {
                        if (notSupportedSource.getMessage() != null && !notSupportedSource.getMessage().equals(""))
                            Utils.showSimpleDialog(getString(R.string.dialog_title), notSupportedSource.getMessage(), null, getActivity(), null);
                        else
                            Utils.showSimpleDialog(getString(R.string.dialog_title), "Ocorreu um problema. Por favor escolher novo arquivo.", null, getActivity(), null);
                    }
                } catch (Exception e) {
                    assert getTag() != null;
                    if (getTag().equals("changePhoto")) {
                        mOnChangePhotoListener.onChangePhoto(e.getMessage());
                    } else {
                        if(e.getMessage() != null && !e.getMessage().equals(""))
                            Utils.showSimpleDialog(getString(R.string.dialog_title), e.getMessage(), null, getActivity(), null);
                        else
                            Utils.showSimpleDialog(getString(R.string.dialog_title), "Ocorreu um problema, favor escolher novo arquivo.", null, getActivity(), null);
                    }
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        Context context = FahzApplication.getAppContext();
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,          /* prefix */
                ".jpg",          /* suffix */
                storageDir              /* directory */
        );
    }

    private File resizeAndCompressFile(Bitmap bitmap, String fileName) {
        File file = new File(fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            if (getTag() != null) {
                if (getTag().equals("changePhoto")) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, false);
                }
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);

            fileOutputStream.close();
            fileOutputStream.flush();
        } catch (IOException e) {
            LogUtils.error(getClass().getSimpleName(), e);
        }

        return file;
    }

    private boolean isDeviceSupportCamera() {
        return getContext() != null && getContext().getPackageManager() != null &&
                getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String resource = null;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = FahzApplication.getAppContext().getContentResolver().query(contentUri,
                filePathColumn, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            resource = cursor.getString(column_index);
            cursor.close();
        }
        return resource;
    }

    private void changePhoto() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Enviando foto para servidor, aguarde um momento");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), mFile);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", mFile.getName(), reqFile);
        MultipartBody.Part cpfBody = MultipartBody.Part.createFormData("cpf", mCpf);
        APIService apiService = ServiceGenerator.createService(APIService.class);
        Call<ProfileImage> call = apiService.changePicture(fileBody, cpfBody);
        call.enqueue(new Callback<ProfileImage>() {
            @Override
            public void onResponse(@NonNull Call<ProfileImage> call, @NonNull Response<ProfileImage> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                progressDialog.dismiss();
                ProfileImage profilePicture = response.body();
                assert profilePicture != null;
                if (response.isSuccessful() && profilePicture.commited) {
                    mOnChangePhotoListener.onChangePhoto("");
                } else {
                    if(getContext()!= null && profilePicture.messageIdentifier!= null)
                        Toast.makeText(getContext(), profilePicture.messageIdentifier, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileImage> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    //METODO QUE CHAMA O ENDPOINT DE UPLOAD DE FOTO
    private void uploadFile() {

        if (checkFileSize(mFile)) {
            int resID = getResources().getIdentifier("MSG022", "string", mContext.getPackageName());
            String message = getResources().getString(resID);
            Utils.showSimpleDialog(getString(R.string.dialog_title), message, null, getActivity(), null);
        } else {

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Enviando arquivo para servidor, aguarde um momento");
            progressDialog.setCancelable(false);
            progressDialog.show();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(180, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .writeTimeout(180, TimeUnit.SECONDS)
                    .addInterceptor(interceptor).build();

            APIService mAPIService = ServiceGenerator.createService(APIService.class);

            //CONSTRUÇÃO DO OBJETO DE ENVIO DO DOCUMENTO
            String contentType = "";
            try {
                contentType = mFile.toURI().toURL().openConnection().getContentType();
            } catch (IOException e) {
                LogUtils.error(getClass().getSimpleName(), e);
            }

            RequestBody reqFile = RequestBody.create(MediaType.parse(contentType), mFile);
            MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", mFile.getName(), reqFile);
            MultipartBody.Part cpfBody = MultipartBody.Part.createFormData("cpf", mCpf);
            MultipartBody.Part nameBody = MultipartBody.Part.createFormData("name", mFile.getName());
            MultipartBody.Part typeBody;

            //Verifica se é um novo tipo a ser adicionado, caso sim precisa retornar o tipo para o método na activity
            if (mId.equals("0")) {
                if (typeDocuments != null && typeDocuments.size() > 0) {
                    mId = String.valueOf(typeDocuments.get(typeDocumentSpinner.getSelectedItemPosition() - 1).id);
                    type = typeDocuments.get(typeDocumentSpinner.getSelectedItemPosition() - 1);
                }
            }

            typeBody = MultipartBody.Part.createFormData("type", mId);

            Call<okhttp3.ResponseBody> req = mAPIService.uploadDocument(fileBody, cpfBody, typeBody, nameBody);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    progressDialog.dismiss();
                    if (!response.isSuccessful()) {
                        LogUtils.error(TAG, new Gson().toJson(response));
                        return;
                    }
                    try {
                        assert response.body() != null;
                        UploadResponse responseUpload = new Gson().fromJson(response.body().string(), UploadResponse.class);

                        String path = mContext.getCacheDir().toString();
                        if (mFile.getPath().contains(path)) {
                            if (mFile.exists())
                                mFile.delete();
                        }

                        if (mContext instanceof DocumentsActivity)
                            ((DocumentsActivity) mContext).uploadDocumentUpdate(responseUpload, Integer.parseInt(mId), type, mFile.getName(), responseUpload.documentId);
                        else if (mContext instanceof BaseSchoolSuppliesControlActivity)
                            ((BaseSchoolSuppliesControlActivity) mContext).uploadDocumentUpdate(responseUpload, Integer.parseInt(mId), type, mFile.getName(), mCpf, responseUpload.documentId);
                        else if (mContext instanceof BaseScholarshipControlActivity)
                            ((BaseScholarshipControlActivity) mContext).uploadDocumentUpdate(responseUpload, Integer.parseInt(mId), type, mFile.getName(), mCpf, responseUpload.documentId);
                        if (mContext instanceof RequestNewToyActivity)
                            ((RequestNewToyActivity) mContext).uploadDocumentUpdate(responseUpload, Integer.parseInt(mId), type, mFile.getName(), mCpf, responseUpload.documentId);
                        if (mContext instanceof AddAnnualRenewalDocumentsActivity)
                            ((AddAnnualRenewalDocumentsActivity) mContext).uploadDocumentUpdate(responseUpload, Integer.parseInt(mId), type, mFile.getName(), responseUpload.documentId);

                    } catch (IOException e) {
                        LogUtils.error(TAG, e);
                    }
                    LogUtils.verbose(TAG, "Upload ok!");
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    LogUtils.error(TAG, t);
                }
            });
        }
    }

    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            assert getArguments() != null;
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        assert activity != null;
                        activity.finish();
                    })
                    .create();
        }
    }

    public interface OnChangePhotoListener {
        void onChangePhoto(String message);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public File getFileFromGallery(Uri originalUri) throws IOException {

        String nameFile = DocumentsHelper.getFileName(getContext(), originalUri);
        ParcelFileDescriptor parcelFileDescriptor = FahzApplication.getAppContext().getContentResolver().openFileDescriptor(originalUri, "r", null);
        File file = new File(Objects.requireNonNull(getContext()).getCacheDir(), nameFile);

        assert parcelFileDescriptor != null;
        InputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
        OutputStream outputStream = new FileOutputStream(file);

        FileUtils.copy(inputStream, outputStream);

        return file.getAbsoluteFile();
    }

    public boolean checkFileSize(File file) {
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        return (file_size > (MAX_IMAGE_SIZE * 1024));
    }
}
