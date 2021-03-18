package br.com.avanade.fahz.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.activities.benefits.BenefitsControlActivity;
import br.com.avanade.fahz.activities.lgpd.PanelPrivacyTermsActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.dialogs.CheckPendingPoliciesActivity;
import br.com.avanade.fahz.dialogs.CheckPendingTermsActivity;
import br.com.avanade.fahz.fragments.UploadDialogFragment;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.ImageObservable;
import br.com.avanade.fahz.model.ProfileImage;
import br.com.avanade.fahz.model.SchoolSuppliesCanRequest;
import br.com.avanade.fahz.model.lgpdModel.CheckPlatform;
import br.com.avanade.fahz.model.lgpdModel.PoliciesAndTerm;
import br.com.avanade.fahz.model.life.DependentsWithPendingAnnualRenewalBody;
import br.com.avanade.fahz.model.menu.MenuHeader;
import br.com.avanade.fahz.model.response.ListDependentsWithPendingAnnualRenewalResponse;
import br.com.avanade.fahz.network.APIService;
import br.com.avanade.fahz.network.ServiceGenerator;
import br.com.avanade.fahz.util.Constants;
import br.com.avanade.fahz.util.ExpandableListAdapter;
import br.com.avanade.fahz.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.avanade.fahz.util.Constants.CPF_EDIT_DEPENDENT;
import static br.com.avanade.fahz.util.Constants.VIEW_EDIT_DEPENDENT;

public abstract class NavDrawerBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UploadDialogFragment.OnChangePhotoListener {

    public static final String TAG = "NavDrawerBaseActivity";
    public TextView toolbarTitle;
    Toolbar toolbar;
    NavigationView navigationView;

    ImageView imgUser;
    ImageView imgHeader;
    TextView UserName;
    DrawerLayout drawer;
    Drawable menuDrawable;
    FrameLayout content_frame;
    RoundedBitmapDrawable imageDrawable;
    boolean mAdhesaionPlan = false;
    ExpandableListView mExpandableListView;
    boolean cansearch;
    boolean canViewSchoolSupplies;
    boolean canViewDocumentsWithPendingRenewal = false;
    ExpandableListAdapter mExpandableListAdapter;
    boolean drawerOpened = false;
    String TermToCheck = "";
    private Context mContext;
    private ProgressDialog mProgressDialog;
    boolean completedMenu = false;
    private boolean mCanViewDocuments = false;
    public ArrayList<MenuHeader> list = new ArrayList<>();
    public boolean checkPoliciesEndTerms = false;
    List<PoliciesAndTerm> policiesList = new ArrayList<>();
    List<PoliciesAndTerm> termsList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager sessionManager = new SessionManager(this);
        imgUser.invalidate();

        if (!Boolean.valueOf(sessionManager.getPreference(Constants.FIRST_ACCESS_TOKEN))) {
            if (sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
                sessionManager.logout();
            } else {
                UserName.setText(FahzApplication.getInstance().getFahzClaims().getName().split(" ")[0]);
            }
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(layoutResID, true);
    }

    public void setContentView(@LayoutRes int layoutResID, boolean generateMenu) {

        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        SessionManager manager = new SessionManager(this);
        cansearch = Boolean.valueOf(manager.getPreference(Constants.CAN_SEARCH_PICTURE));

        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_nav_drawer_base, null);

        imgUser = rootView.findViewById(R.id.imgUser);
        UserName = rootView.findViewById(R.id.user_name);
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbarTitle = rootView.findViewById(R.id.header_title);
        imgHeader = rootView.findViewById(R.id.imageHeaderView);

        imgHeader.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);

        toolbarTitle.setText(getString(R.string.initialPage));

        //ATUALIZACAO DE IMAGEM COM A JA SALVA
        if (!cansearch) {
            String input = manager.getPreference(Constants.BITMAP_PROFILE);
            byte[] decodedByte = Base64.decode(input, 0);
            Bitmap image = BitmapFactory
                    .decodeByteArray(decodedByte, 0, decodedByte.length);
            if (image != null) {
                imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), image);
                imageDrawable.setCircular(true);
                imageDrawable.setCornerRadius(Math.min(image.getWidth(), image.getHeight()) / 2.0f);

                imgUser.invalidate();
                imgUser.setImageDrawable(null);
                imgUser.setImageURI(null);
                imgUser.setImageDrawable(imageDrawable);

                ImageObservable.getInstance().setProfile(imageDrawable);
            }
        }


        //Criação do navigation drawer
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = rootView.findViewById(R.id.drawer_layout_info);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //Hide Virtual Key Board
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawerOpened = true;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                drawerOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        navigationView = rootView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView.setItemIconTintList(null);

        content_frame = rootView.findViewById(R.id.content_frame);
        LayoutInflater.from(this).inflate(layoutResID, content_frame, true);
        menuDrawable = toolbar.getNavigationIcon();

        mExpandableListView = rootView.findViewById(R.id.navigationmenu);
        list.add(new MenuHeader(5, "Sair"));
        mExpandableListAdapter = new ExpandableListAdapter(NavDrawerBaseActivity.this, list, null);
        mExpandableListView.setAdapter(mExpandableListAdapter);

        if (generateMenu) {
            canRequestSchoolSupplies();
            if (!FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
                checkIfHasDependentWithPendingAnnualDocumentsRenewal();
            }

            mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    Intent intent;
                    LogUtils.debug("NavDrawerBaseActivity", "onGroupClick: groupPosition: " + groupPosition);

                    if (completedMenu) {
                        MenuHeader header = new MenuHeader();

                        MenuHeader group = header.getMenu(groupPosition);

                        switch (group.id) {
                            case 0:
                                intent = new Intent(mContext, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                drawer.closeDrawer(GravityCompat.START);
                                break;
                            case 4:
                                intent = new Intent(mContext, ListTermsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                drawer.closeDrawer(GravityCompat.START);
                                break;
                            case 6:
                                intent = new Intent(mContext, PanelPrivacyTermsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                drawer.closeDrawer(GravityCompat.START);
                                break;
                            case 5:
                                logoutUser();

                                break;
                        }
                    } else {

                        switch (groupPosition) {
                            case 0:
                                logoutUser();
                                break;
                        }
                    }
                    return false;
                }
            });
            mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                }
            });
            mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                }
            });

            mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    LogUtils.debug("NavDrawerBaseActivity", "onChildClick: childPosition " + childPosition);
                    Intent intent = null;
                    PendingIntent pendingIntent = null;
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

                    MenuHeader header = new MenuHeader();
                    MenuHeader group = header.getMenu(groupPosition);

                    br.com.avanade.fahz.model.menu.MenuItem child = (br.com.avanade.fahz.model.menu.MenuItem) mExpandableListAdapter.getChild(groupPosition, childPosition);

                    switch (group.id) {
                        case 1:
                            switch (child.id) {
                                case 0:
                                    if (FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(CPF_EDIT_DEPENDENT, FahzApplication.getInstance().getFahzClaims().getCPF());
                                        bundle.putBoolean(VIEW_EDIT_DEPENDENT, false);
                                        bundle.putBoolean(Constants.IS_REACTIVATE, false);
                                        intent = new Intent(mContext, DependentActivity.class);
                                        intent.putExtras(bundle);
                                        stackBuilder.addParentStack(ProfileActivity.class);
                                        stackBuilder.addNextIntent(intent);
                                        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                        startActivity(intent);
                                    } else {
                                        intent = new Intent(mContext, ProfileActivity.class);
                                        stackBuilder.addParentStack(ProfileActivity.class);
                                        stackBuilder.addNextIntent(intent);
                                        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                        drawer.closeDrawer(GravityCompat.START);
                                    }

                                    break;

                                case 1:
                                    intent = new Intent(mContext, ListDependentsActivity.class);
                                    stackBuilder.addParentStack(ListDependentsActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 5:
                                    intent = new Intent(mContext, InactivateDependentRegisterActivity.class);
                                    stackBuilder.addParentStack(InactivateDependentRegisterActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 2:
                                    intent = new Intent(mContext, DocumentsActivity.class);
                                    if (FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
                                        intent.putExtra(Constants.CONTEXT_DOCUMENT, -1);
                                    }
                                    intent.putExtra(Constants.ORIGIN_CALL_DOCUMENT, DocumentsActivity.OriginCall.FROMMENU);
                                    intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, FahzApplication.getInstance().getFahzClaims().getCPF());

                                    stackBuilder.addParentStack(DocumentsActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 3: //Documentos de Renovação anual
                                    if (canViewDocumentsWithPendingRenewal) {
                                        intent = new Intent(mContext, AnnualRenewalDocumentsActivity.class);
                                        if (FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
                                            intent.putExtra(Constants.CONTEXT_DOCUMENT, -1);
                                        }
                                        intent.putExtra(Constants.CONTEXT_DOCUMENT_CPF, FahzApplication.getInstance().getFahzClaims().getCPF());

                                        stackBuilder.addParentStack(AnnualRenewalDocumentsActivity.class);
                                        stackBuilder.addNextIntent(intent);
                                        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    }
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 4:
                                    intent = new Intent(mContext, ChangePasswordActivity.class);
                                    stackBuilder.addParentStack(ChangePasswordActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                            }
                            break;
                        case 2:
                            switch (child.id) {
                                case 0:
                                    intent = new Intent(mContext, BenefitsControlActivity.class);
                                    intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.HEALTHPLAN);
                                    stackBuilder.addParentStack(BenefitsControlActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 1:
                                    intent = new Intent(mContext, BenefitsControlActivity.class);
                                    intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.DENTALPLAN);
                                    stackBuilder.addParentStack(BenefitsControlActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 2:
                                    intent = new Intent(mContext, BenefitsControlActivity.class);
                                    intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.PHARMACY);
                                    stackBuilder.addParentStack(BenefitsControlActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                            }
                            break;
                        case 3:
                            switch (child.id) {
                                case 0:

                                    intent = new Intent(mContext, BenefitsControlActivity.class);
                                    intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.SCHOLARSHIP);
                                    stackBuilder.addParentStack(BenefitsControlActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 1:

                                    intent = new Intent(mContext, BenefitsControlActivity.class);
                                    intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.TOY);
                                    stackBuilder.addParentStack(BenefitsControlActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 2:

                                    intent = new Intent(mContext, BenefitsControlActivity.class);
                                    intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.CHRISTMAS);
                                    stackBuilder.addParentStack(BenefitsControlActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                                case 3:
                                    intent = new Intent(mContext, BenefitsControlActivity.class);
                                    intent.putExtra(Constants.BENEFITIS_CONTROL, BenefitsControlActivity.BenefitsFragment.SCHOOLSUPPLIES);
                                    stackBuilder.addParentStack(BenefitsControlActivity.class);
                                    stackBuilder.addNextIntent(intent);
                                    pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                    drawer.closeDrawer(GravityCompat.START);
                                    break;
                            }
                            break;
                    }

                    try {
                        if (pendingIntent != null)
                            pendingIntent.send();
                        else
                            Toast.makeText(mContext, "Em desenvolvimento", Toast.LENGTH_LONG).show();
                        //overridePendingTransition(0, 0);
                    } catch (Exception ex) {
                        Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            });

            imgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionManager manager = new SessionManager(NavDrawerBaseActivity.this);
                    manager.createPreference(Constants.CAN_SEARCH_PICTURE, "true");

                    Bundle bundle = new Bundle();
                    bundle.putString("cpf", FahzApplication.getInstance().getFahzClaims().getCPF());
                    bundle.putString("title", "Alterar imagem do perfil");
                    UploadDialogFragment uploadDialogFragment = new UploadDialogFragment();
                    uploadDialogFragment.setArguments(bundle);
                    uploadDialogFragment.show(getSupportFragmentManager(), "changePhoto");
                }
            });
        } else {
            mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (completedMenu) {
                        MenuHeader header = new MenuHeader();
                        MenuHeader group = header.getMenu(groupPosition);

                        switch (group.id) {
                            case 5:
                                logoutUser();

                                break;
                        }
                    } else {

                        switch (groupPosition) {
                            case 0:
                                logoutUser();
                                break;
                        }
                    }
                    return false;
                }
            });
            mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                }
            });
            mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                }
            });

            mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    return false;
                }
            });


            imgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionManager manager = new SessionManager(NavDrawerBaseActivity.this);
                    manager.createPreference(Constants.CAN_SEARCH_PICTURE, "true");

                    Bundle bundle = new Bundle();
                    bundle.putString("cpf", FahzApplication.getInstance().getFahzClaims().getCPF());
                    bundle.putString("title", "Alterar imagem do perfil");
                    UploadDialogFragment uploadDialogFragment = new UploadDialogFragment();
                    uploadDialogFragment.setArguments(bundle);
                    uploadDialogFragment.show(getSupportFragmentManager(), "changePhoto");
                }
            });
        }

        super.setContentView(rootView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void checkIfHasDependentWithPendingAnnualDocumentsRenewal() {
        setLoading(true);

        APIService apiService = ServiceGenerator.createService(APIService.class);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        Call<ListDependentsWithPendingAnnualRenewalResponse> call = apiService.dependentsWithPendingAnnualRenewal(new DependentsWithPendingAnnualRenewalBody(cpf));
        call.enqueue(new Callback<ListDependentsWithPendingAnnualRenewalResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListDependentsWithPendingAnnualRenewalResponse> call, @NonNull Response<ListDependentsWithPendingAnnualRenewalResponse> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    ListDependentsWithPendingAnnualRenewalResponse request = response.body();
                    canViewDocumentsWithPendingRenewal = (request != null && request.getCommited()) && request.getResult() > 0;

                    if (canViewDocumentsWithPendingRenewal) {
                        HashMap<MenuHeader, List<br.com.avanade.fahz.model.menu.MenuItem>> items =
                                br.com.avanade.fahz.model.menu.MenuItem.ListMenuItem(canViewSchoolSupplies, canViewDocumentsWithPendingRenewal, mCanViewDocuments);

                        mExpandableListAdapter = new ExpandableListAdapter(NavDrawerBaseActivity.this, MenuHeader.menuHeaders, items);
                        mExpandableListView.setAdapter(mExpandableListAdapter);
                    }
                    setLoading(false);
                } else {
                    try {
                        if (response.code() == 401) {
                            logoutUser();
                            return;
                        }
                        String data = response.errorBody() != null ? response.errorBody().string() : null;
                        JSONObject jObjError = new JSONObject(data);
                        String message = jObjError.getString("Message");
                        LogUtils.error(TAG, "onFailure: " + message);
                    } catch (Exception ex) {
                        LogUtils.error(TAG, ex);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListDependentsWithPendingAnnualRenewalResponse> call, @NonNull Throwable t) {
                LogUtils.error(TAG, t);
                setLoading(false);
            }
        });
    }

    private void canRequestSchoolSupplies() {
        setLoading(true);
        APIService apiService = ServiceGenerator.createService(APIService.class);

        Call<JsonElement> call = apiService.checkCanRequestBenefit(new CPFInBody(FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject().has("messageIdentifier")) {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        setLoading(false);
                        LogUtils.debug(TAG, "onFailure: " + responseCommit.messageIdentifier);
                    } else {
                        SchoolSuppliesCanRequest request = new Gson().fromJson((response.body().getAsJsonObject()), SchoolSuppliesCanRequest.class);
                        canViewSchoolSupplies = request.getUserRequestSchool().getIsCard() || request.getUserRequestSchool().getIsRefund();

                        MenuHeader header = new MenuHeader();
                        HashMap<MenuHeader, List<br.com.avanade.fahz.model.menu.MenuItem>> items =
                                br.com.avanade.fahz.model.menu.MenuItem.ListMenuItem(canViewSchoolSupplies, canViewDocumentsWithPendingRenewal, mCanViewDocuments);

                        mExpandableListAdapter = new ExpandableListAdapter(NavDrawerBaseActivity.this, MenuHeader.menuHeaders, items);
                        mExpandableListView.setAdapter(mExpandableListAdapter);
                        completedMenu = true;
                    }

                    setLoading(false);

                } else {
                    try {
                        if (response.code() == 401) {
                            logoutUser();
                            return;
                        }
                        String data = null;
                        if (response.errorBody() != null) {
                            data = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(data);
                            String message = jObjError.getString("Message");
                            LogUtils.error(TAG, "onFailure: " + message);
                        }
                    } catch (Exception ex) {
                        LogUtils.error(TAG, ex);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                LogUtils.error(TAG, t);
                setLoading(false);
            }
        });
    }

    public void setImageHeaderVisibility(boolean setVisible) {
        if (setVisible) {
            imgHeader.setVisibility(View.VISIBLE);
            toolbarTitle.setVisibility(View.GONE);
        } else {
            imgHeader.setVisibility(View.GONE);
            toolbarTitle.setVisibility(View.VISIBLE);
        }
        //textCancelInfo.setVisibility(View.GONE);
    }

    public void setMenuVisible(boolean setVisible) {
        if (setVisible) {
            toolbar.setNavigationIcon(menuDrawable);
        } else {
            toolbar.setNavigationIcon(null);
        }

        //textCancelInfo.setVisibility(View.GONE);
    }

    public void setCancelOperation(boolean setVisible) {
        mAdhesaionPlan = setVisible;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_benefit, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white01)), 0, spanString.length(), 0);

            item.setTitle(spanString);
        }
        return mAdhesaionPlan;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cancel:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mContext = FahzApplication.getAppContext();
        mProgressDialog = new ProgressDialog(this);
        setLoading(false);

        getProfilePicture(FahzApplication.getInstance().getFahzClaims().getCPF());

        mCanViewDocuments = true;
        checkPendingPolicies();
        //validateTermOfUse();
    }

    private void logoutUser() {
        setLoading(true);
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.logout();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void getProfilePicture(String cpf) {
        SessionManager manager = new SessionManager(this);
        cansearch = Boolean.parseBoolean(manager.getPreference(Constants.CAN_SEARCH_PICTURE));
        if (cansearch) {
            final ImageObservable mIObservable = ImageObservable.getInstance();
            APIService apiService = ServiceGenerator.createService(APIService.class);
            Call<ProfileImage> call = apiService.queryProfileImage(new CPFInBody(cpf));
            call.enqueue(new Callback<ProfileImage>() {
                @Override
                public void onResponse(@NonNull Call<ProfileImage> call, @NonNull Response<ProfileImage> response) {
                    FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                    ProfileImage profileImage = response.body();
                    if (response.isSuccessful() && profileImage != null) {
                        if (profileImage.commited) {
                            Date date = new Date();

                            byte[] byteImage = Base64.decode(profileImage.Data, Base64.DEFAULT);
                            Bitmap imageBitmap = ((BitmapDrawable) imgUser.getDrawable()).getBitmap();
                            imageBitmap = BitmapFactory.decodeByteArray(byteImage,0,byteImage.length);

                            imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.min(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);

                            imgUser.invalidate();
                            imgUser.setImageDrawable(null);
                            imgUser.setImageURI(null);
                            imgUser.setImageDrawable(imageDrawable);

                            mIObservable.setProfile(imageDrawable);

                            Bitmap immage = imageDrawable.getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] b = baos.toByteArray();
                            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

                            SessionManager manager = new SessionManager(NavDrawerBaseActivity.this);
                            manager.createPreference(Constants.CAN_SEARCH_PICTURE, "false");
                            manager.createPreference(Constants.BITMAP_PROFILE, imageEncoded);

                        } else {
                            Toast.makeText(mContext, profileImage.messageIdentifier, Toast.LENGTH_LONG).show();
                            imgUser.setImageResource(R.drawable.user);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProfileImage> call, @NonNull Throwable t) {
                    LogUtils.error(TAG, t);
                    imgUser.setImageResource(R.drawable.user);
                }
            });
        }
    }

    @Override
    public void onChangePhoto(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        } else {
            getProfilePicture(FahzApplication.getInstance().getFahzClaims().getCPF());
        }
    }

    private void setLoading(Boolean loading) {
        try {
            if (loading) {
                if (mProgressDialog != null) {
                    mProgressDialog.setMessage("Aguarde um momento");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }
            } else {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        } catch (Exception ex) {
            LogUtils.error("NavDrawerBaseActivity", ex);
        }
    }
/*
    //MÈTODO QUE VERIFICA SE O USUARIO JÀ ACEITOU O TERMO DE USO PADRÃO
    public void validateTermOfUse() {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        String value = FahzApplication.getInstance().getFahzClaims().getRoles();
        if (value.contains(Dependent_Role))
            TermToCheck = Constants.TERMS_OF_USE_CODE_GENERAL_DEP;
        else
            TermToCheck = Constants.TERMS_OF_USE_CODE_GENERAL;

        Call<Object> call = mAPIService.checkUserAcceppetedTerm(new TermCheckBody(TermToCheck, FahzApplication.getInstance().getFahzClaims().getCPF()));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {
                    setLoading(false);

                    if (response.body() instanceof LinkedTreeMap) {

                        if (((LinkedTreeMap) response.body()).containsKey("accepted")) {
                            boolean accepted = (boolean) ((LinkedTreeMap) response.body()).get("accepted");

                            if (!accepted) {
                                Intent intent = new Intent(mContext, TermsOfUseActivity.class);
                                intent.putExtra(Constants.TERMS_OF_USE_SELECTED, TermToCheck);
                                startActivityForResult(intent, Constants.TERMS_OF_USE_RESULT);
                            }

                        } else {
                            Toast.makeText(mContext, getString(R.string.problemServer), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    setLoading(false);
                    String message;
                    String code;
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        message = jObjError.getString("Message");
                        code = jObjError.getString("Code");
                        if (code.equals(Constants.ERROR_UNAUTHORIZED) &&
                                message.equals(Constants.ERROR_DESCRIPTION_UNAUTHORIZED)) {
                            SessionManager sessionManager = new SessionManager(NavDrawerBaseActivity.this);
                            if (!Boolean.valueOf(sessionManager.getPreference(Constants.FIRST_ACCESS_TOKEN))) {
                                logoutUser();
                            }
                        } else {
                            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                setLoading(false);
                if (t instanceof SocketTimeoutException)
                    Toast.makeText(mContext, getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), Toast.LENGTH_LONG).show();
                else if (t instanceof UnknownHostException)
                    Toast.makeText(mContext, getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }*/

    public boolean isDrawerOpened() {
        return drawerOpened;
    }

    //Metido que verifica se ha politicas pendentes para o usuário aceitar
    public void checkPendingPolicies() {

        setLoading(true);

        APIService mAPIService = ServiceGenerator.createService(APIService.class);
        String cpf = FahzApplication.getInstance().getFahzClaims().getCPF();
        Call<JsonElement> call = mAPIService.getPendingPolicies(new CheckPlatform(cpf, Constants.FEATURE_FLAG_PLATFORM));
        call.enqueue(new Callback<JsonElement>() {
            @SuppressLint("LongLogTag")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                FahzApplication.getInstance().setToken(response.raw().headers().get("token"));
                if (response.isSuccessful()) {

                    if (!response.body().getAsJsonObject().has("messageIdentifier")) {

                        List<PoliciesAndTerm> policiesAndTermList = new Gson().fromJson((response.body().getAsJsonObject().get("policiesAndTerms")), new TypeToken<ArrayList<PoliciesAndTerm>>() {
                        }.getType());

                        if (policiesAndTermList.size() > 0) {

                            //Filtra somente as politicas
                            policiesList.clear();
                            termsList.clear();

                            for (PoliciesAndTerm policiesAndTerm : policiesAndTermList) {
                                if (policiesAndTerm.documentType == 2) {
                                    policiesList.add(policiesAndTerm);
                                }
                            }

                            //Filtra somente os termos
                            for (PoliciesAndTerm item : policiesAndTermList) {
                                if (item.documentType == 1) {
                                    termsList.add(item);
                                }
                            }

                            if (policiesList.size() > 0) {
                                Intent intent = new Intent(mContext, CheckPendingPoliciesActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("policiesAndTermList", (Serializable) policiesList);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, Constants.LIST_OF_POLICIES);

                            } else {

                                Intent intent = new Intent(mContext, CheckPendingTermsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("policiesAndTermList", (Serializable) termsList);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, Constants.LIST_OF_TERMS);
                            }
                        }
                    } else {
                        CommitResponse responseCommit = new Gson().fromJson((response.body().getAsJsonObject()), CommitResponse.class);
                        Toast.makeText(mContext, responseCommit.messageIdentifier, Toast.LENGTH_LONG).show();
                    }

                    setLoading(false);

                } else {
                    setLoading(false);
                    String message;
                    String code;
                    try {
                        String data = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(data);
                        message = jObjError.getString("Message");
                        code = jObjError.getString("Code");
                        if (code.equals(Constants.ERROR_UNAUTHORIZED) &&
                                message.equals(Constants.ERROR_DESCRIPTION_UNAUTHORIZED)) {
                            SessionManager sessionManager = new SessionManager(NavDrawerBaseActivity.this);
                            if (!Boolean.valueOf(sessionManager.getPreference(Constants.FIRST_ACCESS_TOKEN))) {
                                logoutUser();
                            }
                        } else {
                            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                setLoading(false);
                if (t instanceof SocketTimeoutException)
                    Toast.makeText(mContext, getResources().getString(getResources().getIdentifier("MSG362", "string", getPackageName())), Toast.LENGTH_LONG).show();
                else if (t instanceof UnknownHostException)
                    Toast.makeText(mContext, getResources().getString(getResources().getIdentifier("MSG361", "string", getPackageName())), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.LIST_OF_POLICIES) {
            if (resultCode == Activity.RESULT_OK) {

                if (termsList.size() > 0) {
                    Intent intent = new Intent(mContext, CheckPendingTermsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("policiesAndTermList", (Serializable) termsList);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, Constants.LIST_OF_TERMS);
                }
            }
        }
    }
}
