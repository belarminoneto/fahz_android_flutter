package br.com.avanade.fahz.network;

import com.google.gson.JsonElement;

import java.util.List;

import br.com.avanade.fahz.model.AddDependentRequest;
import br.com.avanade.fahz.model.AdhesionPlanRequest;
import br.com.avanade.fahz.model.BenefitList;
import br.com.avanade.fahz.model.CPFInBody;
import br.com.avanade.fahz.model.CalculateRefundRequest;
import br.com.avanade.fahz.model.CancelBenefit;
import br.com.avanade.fahz.model.ChangeBenefit;
import br.com.avanade.fahz.model.ChangePasswordRequest;
import br.com.avanade.fahz.model.ChangePasswordResponse;
import br.com.avanade.fahz.model.CommitResponse;
import br.com.avanade.fahz.model.DependentHolder;
import br.com.avanade.fahz.model.DependentLife;
import br.com.avanade.fahz.model.DependentResponse;
import br.com.avanade.fahz.model.Document;
import br.com.avanade.fahz.model.DocumentDeleteRequest;
import br.com.avanade.fahz.model.DocumentDownload;
import br.com.avanade.fahz.model.FinancialPlanUpdate;
import br.com.avanade.fahz.model.FollowupUpdate;
import br.com.avanade.fahz.model.HistoryModel;
import br.com.avanade.fahz.model.InactivateDependent;
import br.com.avanade.fahz.model.IncludeBenefitToyModel;
import br.com.avanade.fahz.model.IncludeBenefitToyResponse;
import br.com.avanade.fahz.model.InsertTermOfUseResquest;
import br.com.avanade.fahz.model.ProfileImage;
import br.com.avanade.fahz.model.RequestNewPersonalCard;
import br.com.avanade.fahz.model.ResetPasswordRequest;
import br.com.avanade.fahz.model.ResetPasswordResponse;
import br.com.avanade.fahz.model.ScholarshipRequest;
import br.com.avanade.fahz.model.SendAddressChristmas;
import br.com.avanade.fahz.model.SendAnnualRenewalDocumentsForApprovalRequest;
import br.com.avanade.fahz.model.SendReceiptRequest;
import br.com.avanade.fahz.model.SignupRequest;
import br.com.avanade.fahz.model.SignupResponse;
import br.com.avanade.fahz.model.States;
import br.com.avanade.fahz.model.TitularResponse;
import br.com.avanade.fahz.model.UrlTeleHealthRequest;
import br.com.avanade.fahz.model.UserBenefits;
import br.com.avanade.fahz.model.ValidateTokenRequest;
import br.com.avanade.fahz.model.ZipResponse;
import br.com.avanade.fahz.model.benefits.CanInactivePlanBody;
import br.com.avanade.fahz.model.benefits.OperatorsByBenefitBody;
import br.com.avanade.fahz.model.benefits.healthplan.medicalrecord.ConsultationsPerformed;
import br.com.avanade.fahz.model.document.DocumentTypeBody;
import br.com.avanade.fahz.model.document.DocumentsMaterialSchoolBody;
import br.com.avanade.fahz.model.document.QueryDocumentTypeBody;
import br.com.avanade.fahz.model.document.QueryDocumentTypeGenericBody;
import br.com.avanade.fahz.model.document.TypesWithoutDocumentsBody;
import br.com.avanade.fahz.model.externalaccess.ExternalAccessUrlBody;
import br.com.avanade.fahz.model.history.GetRequestBody;
import br.com.avanade.fahz.model.invoicehandling.ListExtractUsageBody;
import br.com.avanade.fahz.model.lgpdModel.AcceptData;
import br.com.avanade.fahz.model.lgpdModel.CancelBenefitsFromTermDto;
import br.com.avanade.fahz.model.lgpdModel.CheckPlatform;
import br.com.avanade.fahz.model.lgpdModel.GetListOfTerms;
import br.com.avanade.fahz.model.lgpdModel.MedicalRecordsDetails;
import br.com.avanade.fahz.model.lgpdModel.NotificationAnswerResponse;
import br.com.avanade.fahz.model.lgpdModel.NotificationResponse;
import br.com.avanade.fahz.model.lgpdModel.PolicyResponse;
import br.com.avanade.fahz.model.lgpdModel.PrivacyControlResponse;
import br.com.avanade.fahz.model.lgpdModel.PrivacyTerms;
import br.com.avanade.fahz.model.lgpdModel.TermOfService;
import br.com.avanade.fahz.model.life.DependentFilterBody;
import br.com.avanade.fahz.model.life.DependentHolderBody;
import br.com.avanade.fahz.model.life.DependentsInBenefitBody;
import br.com.avanade.fahz.model.life.DependentsOutOfBenefitBody;
import br.com.avanade.fahz.model.life.DependentsWithPendingAnnualRenewalBody;
import br.com.avanade.fahz.model.life.ListHolderAndDependentsBody;
import br.com.avanade.fahz.model.prontmedappointment.ListOfAppointmentsBody;
import br.com.avanade.fahz.model.prontmedappointment.SearchForDoctosBody;
import br.com.avanade.fahz.model.response.AnnualDocumentsByDependentResponse;
import br.com.avanade.fahz.model.response.BankListResponse;
import br.com.avanade.fahz.model.response.CalculateRefundResponse;
import br.com.avanade.fahz.model.response.CanRequestToyResponse;
import br.com.avanade.fahz.model.response.CanSendReceiptResponse;
import br.com.avanade.fahz.model.response.CityListResponse;
import br.com.avanade.fahz.model.response.CivilStateListResponse;
import br.com.avanade.fahz.model.response.ExtractUsageResponse;
import br.com.avanade.fahz.model.response.FamilyGroupListResponse;
import br.com.avanade.fahz.model.response.LifesAndDependents;
import br.com.avanade.fahz.model.response.ListDependentsResponse;
import br.com.avanade.fahz.model.response.ListDependentsWithPendingAnnualRenewalResponse;
import br.com.avanade.fahz.model.response.MajorDependentsForAnnualDocumentRenewalResponse;
import br.com.avanade.fahz.model.response.OperatorResponse;
import br.com.avanade.fahz.model.response.SendAnnualRenewalDocumentsForApprovalResponse;
import br.com.avanade.fahz.model.response.TypesWithoutDocumentsResponse;
import br.com.avanade.fahz.model.response.prontmed.CancelScheduleResponse;
import br.com.avanade.fahz.model.response.prontmed.DoctorListResponse;
import br.com.avanade.fahz.model.response.prontmed.ListOfAppointmentsResponse;
import br.com.avanade.fahz.model.response.prontmed.ScheduleAppointmentRequest;
import br.com.avanade.fahz.model.response.prontmed.ScheduleAppointmentResponse;
import br.com.avanade.fahz.model.response.prontmed.SearchAppointmentResponse;
import br.com.avanade.fahz.model.response.prontmed.SpecialityListResponse;
import br.com.avanade.fahz.model.schoolsupplies.PlanInformationBody;
import br.com.avanade.fahz.model.schoolsupplies.SchoolBenefitStartBody;
import br.com.avanade.fahz.model.servicemodels.LoginRequest;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

@SuppressWarnings("unused")
public interface APIService {
    @POST("services/serpro")
    Call<JsonElement> serpro(@Body CPFInBody body);

    @GET("services/cep")
    Call<ZipResponse> cep(@Query("cep") String cep);

    @POST("user/login")
    Call<JsonElement> login(@Body LoginRequest loginRequest);

    @POST("user/resetpassword")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @POST("user/changepassword")
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest changePasswordRequest);

    @POST("ExternalAccess/externalAccessUrl")
    Call<JsonElement> getURL(@Body ExternalAccessUrlBody body);

    @POST("user/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @POST("documents/types")
    Call<Document> queryDocumentType(@Body QueryDocumentTypeBody body);

    @POST("documents/types")
    Call<Document> queryDocumentTypeGeneric(@Body QueryDocumentTypeGenericBody body);



    @GET("documents/history")
    Call<TypesWithoutDocumentsResponse> getDocumentsByHistoryId(@Query("historyId") String historyId);

    @POST("life/dados")
    Call<TitularResponse> getDataTitular(@Body CPFInBody body);

    @POST("life/edit")
    Call<CommitResponse> editTitular(@Body TitularResponse titular, @Query("reactivate") boolean reactivate, @Query("generateToken") boolean generateToken,
                                     @Query("methodType") String method);

    @Multipart
    @POST("document/upload")
    Call<okhttp3.ResponseBody> uploadDocument(@Part MultipartBody.Part file, @Part MultipartBody.Part cpf, @Part MultipartBody.Part type, @Part MultipartBody.Part name);

    @POST("document/delete")
    Call<CommitResponse> deleteDocument(@Body DocumentDeleteRequest request);

    @POST("documents/DocumentTypes")
    Call<Document> queryDocumentType(@Body DocumentTypeBody body);

    @GET("documents/download")
    Call<DocumentDownload> downloadDocument(@Query("file") String file, @Query("cpf") String cpf);

    @POST("life/dependents")
    Call<DependentHolder> queryDependentHolder(@Body DependentHolderBody body);

    @POST("life/dependent/add")
    Call<DependentResponse> addDependent(@Body AddDependentRequest addDependentRequest, @Query("generateToken") boolean generateToken,
                                         @Query("validationMethod") String validationMethod);

    @POST("life/dependent/edit")
    Call<CommitResponse> editDependent(@Query("reactivate") boolean reactivate, @Body DependentLife dependentLife,
                                       @Query("generateToken") boolean generateToken, @Query("validationMethod") String validationMethod);

    @POST("/life/dependents/inactivate")
    Call<CommitResponse> inactiveDependent(@Body InactivateDependent inactivateDependent);

    @POST("life/dependent")
    Call<DependentLife> queryCPFDependent(@Body CPFInBody body);

    @GET("benefits/benefits")
    Call<BenefitList> getBenefitList();

    @POST("benefits/userbenefits")
    Call<UserBenefits> queryUserBenefits(@Body CPFInBody body);

    @POST("user/getprofileimage")
    Call<ProfileImage> queryProfileImage(@Body CPFInBody body);

    @Multipart
    @POST("user/changepicture")
    Call<ProfileImage> changePicture(@Part MultipartBody.Part file, @Part MultipartBody.Part cpf);

/*
    @POST("terms/check")
    Call<Object> checkUserAcceppetedTerm(@Body TermCheckBody body);
*/

    @GET("/terms/getTerm")
    Call<JsonElement> getTermByCode(@Query("code") String code);

    @POST("/terms/Accept")
    Call<JsonElement> accepetTerm(@Body InsertTermOfUseResquest request);

    @POST("/benefits/CancelFromTerm")
    Call<JsonElement> cancelBenefitsFromTerm(@Body CancelBenefitsFromTermDto cancelBenefitsFromTermDto);

    @GET("/life/getcivilstates")
    Call<CivilStateListResponse> getCivilStates();

    @GET("/life/banks")
    Call<BankListResponse> getBanks();

    @GET("/life/states")
    Call<States> getStates();

    @GET("/services/cities")
    Call<CityListResponse> getCityByState(@Query("uf") String uf, @Query("search") String search);

    @GET("/life/kinship")
    Call<JsonElement> getKinships();

    @GET("/life/discharges")
    Call<JsonElement> getDischarges();

    @POST("/benefits/operators")
    Call<JsonElement> getOperatorsByBenefit(@Body OperatorsByBenefitBody body);

    @POST("/health/optin")
    Call<CommitResponse> adhesionHealthPlan(@Body AdhesionPlanRequest request);

    @POST("/life/dependents/getOutOfBenefit")
    Call<JsonElement> getDependentesOutOfBenefit(@Body DependentsOutOfBenefitBody body);

    @POST("/health/optindepedent")
    Call<CommitResponse> adhesionHealthPlanDependent(@Body CPFInBody body);

    @GET("/messages/get")
    Call<JsonElement> getMessages(@Query("read") Object read, @Query("top") int top, @Query("skip") int skip);

    @POST("/messages/read")
    Call<CommitResponse> setReadMessage(@Query("messageId") String messageId);

    @POST("/messages/getUnreadCount")
    Call<JsonElement> getUnreadMessageCount(@Body CPFInBody body);

    @POST("/history/getrequests")
    Call<JsonElement> getRequests(@Body GetRequestBody body);

    @POST("/benefits/cancel")
    Call<CommitResponse> inactivateBenefit(@Body CancelBenefit request);

    @GET("/benefits/cards")
    Call<JsonElement> getPlanCards(@Query("benefit") int benefit);

    @POST("/life/dependents/getInBenefit")
    Call<JsonElement> getDependentesInBenefit(@Body DependentsInBenefitBody body);

    @POST("/dental/optin")
    Call<CommitResponse> adhesionDentalPlan(@Body AdhesionPlanRequest request);

    @POST("/dental/optindependent")
    Call<CommitResponse> adhesionDentalPlanDependent(@Body CPFInBody body);

    @POST("/benefits/changebenefit")
    Call<CommitResponse> changeBenefit(@Body ChangeBenefit request);

    @POST("/pharmacy/optin")
    Call<CommitResponse> adhesionPharmacyPlan(@Body AdhesionPlanRequest request);

    @POST("/pharmacy/optindependent")
    Call<CommitResponse> adhesionPharmacyPlanDependent(@Body CPFInBody body);

    @POST("/schoolsupplies/canrequestrefundandcard")
    Call<JsonElement> checkCanRequestBenefit(@Body CPFInBody body);

    @POST("/services/lifebasicdata")
    Call<JsonElement> getLifeBasicInformation(@Body CheckPlatform body);

    @POST("/schoolsupplies/startbenefitrequest")
    Call<JsonElement> getSchoolBenefitStart(@Body SchoolBenefitStartBody body);

    @GET("/schoolsupplies/schooling")
    Call<JsonElement> getSchoolings(@Query("type") int type);

    @POST("/schoolsupplies/includebenefitcard")
    Call<CommitResponse> includeBenefitCard(@Body SendReceiptRequest requsts);

    @POST("/schoolsupplies/includebenefitrefundandreceips")
    Call<JsonElement> includebenefitrefundandreceips(@Body SendReceiptRequest requsts, @Query("IsRefund") boolean isRefund);

    @POST("/schoolsupplies/cansendreceipts")
    Call<CanSendReceiptResponse> canSendReceipts(@Body CPFInBody body);

    @POST("/schoolsupplies/familycansendreceipts")
    Call<JsonElement> whoCanSendRecipt(@Body CPFInBody body);

    @POST("/schoolsupplies/planinformation")
    Call<JsonElement> getPlanInformation(@Body PlanInformationBody body);

    @POST("/terms/getallbyuser")
    Call<JsonElement> getAccepedtedTerms(@Body CPFInBody body);

    @POST("/document/getdocumentmaterialschool")
    Call<JsonElement> getDocumentsMaterialSchool(@Body DocumentsMaterialSchoolBody body);

    @POST("/scholarship/canrequestscholarship")
    Call<JsonElement> getCanRequestScholarship(@Body CPFInBody body);

    @POST("/scholarship/getscholarshipdata")
    Call<JsonElement> getScholarshipData(@Body CPFInBody body);

    @POST("/scholarship/addbenefit")
    Call<JsonElement> includeScholarshipBenefit(@Body ScholarshipRequest request);

    @POST("/scholarship/canrequestrefund")
    Call<JsonElement> getCanRequestRefund(@Body CPFInBody body);

    @GET("/scholarship/getfinancialsplanitens")
    Call<JsonElement> GetFinancialsPlanItens(@Query("planId") String id);

    @POST("/scholarship/updatefinancialplan")
    Call<JsonElement> updateFinancialPlan(@Body FinancialPlanUpdate item);

    @POST("/scholarship/canstartcoursefollowup")
    Call<JsonElement> canStartFollowUp(@Body CPFInBody body);

    @GET("/scholarship/startfollowup")
    Call<JsonElement> startFollowup(@Query("scholarshipId") String scholarshipId);

    @POST("/scholarship/generatefolowupdocuments")
    Call<JsonElement> GenerateFolowupDocuments(@Body FollowupUpdate items);

    @POST("/health/validateoperatortorequestnewcard")
    Call<OperatorResponse> getCurrentOperator(@Body CPFInBody body);

    @GET("/service/getreasonrequestpersonalcard")
    Call<JsonElement> GetReasonRequestPersonalCard();

    @POST("/health/startrequestnewcard")
    Call<JsonElement> StartRequestNewCard(@Body CPFInBody body);

    @POST("/health/createrequestnewcard")
    Call<CommitResponse> generateRequestNewCard(@Body List<RequestNewPersonalCard> items);

    @POST("/history/historydocuments")
    Call<CommitResponse> generateHistoryDocument(@Body HistoryModel request);

    @POST("/benefits/caninactiveplan")
    Call<JsonElement> checkCanInactivePlan(@Body CanInactivePlanBody body);

    @POST("/scholarship/calculateRefund")
    Call<CalculateRefundResponse> calculateRefund(@Body CalculateRefundRequest request);

    @POST("/xmasbasket/startchooseaddress")
    Call<JsonElement> startChooseAddress(@Body CPFInBody body);

    @GET("/life/workspacesxmas")
    Call<JsonElement> getXmasWorkspace();

    @GET("/life/subsidiariesbyworkspace")
    Call<JsonElement> getXmasSubsidiaries(@Query("idWorkspace") int idWorkspace);

    @GET("/life/companiesbysubsidiary")
    Call<JsonElement> getXmasComapanies(@Query("idSubsidiary") String idSubsidiary);

    @POST("/toy/canrequesttoy")
    Call<CanRequestToyResponse> canRequestToy(@Body CPFInBody body);

    @POST("/toy/getlistdependents")
    Call<ListDependentsResponse> listDependents(@Body CPFInBody body);

    @POST("/toy/includebenefittoy")
    Call<IncludeBenefitToyResponse> includeBenefitToy(@Body List<IncludeBenefitToyModel> request);

    @POST("/xmasbasket/chooseaddress")
    Call<CommitResponse> sendXmasAddress(@Body SendAddressChristmas address);

    @POST("/toy/includebenefittoyWithDocuments")
    Call<IncludeBenefitToyResponse> includeBenefitToyDocument(@Body List<IncludeBenefitToyModel> request);

    @POST("invoicehandling/GetUsageEntrySearch")
    Call<ExtractUsageResponse> listExtractUsage(@Body ListExtractUsageBody body);

    @POST("life/HolderAndDependents")
    Call<LifesAndDependents> listHolderAndDependents(@Body ListHolderAndDependentsBody body);

    @POST("prontmedappointment/ScheduleAppointment")
    Call<ScheduleAppointmentResponse> prontmedScheduleAppointment(@Body ScheduleAppointmentRequest body);

    @DELETE("prontmedappointment/DeleteSchedule")
    Call<CancelScheduleResponse> prontmedCancelSchedule(@Query("IdAppointment") long appointmentId);

    @GET("prontmedappointment/SearchAppointment")
    Call<SearchAppointmentResponse> prontMedSearchAppointment(@Query("careProviderId") String careProviderId, @Query("date") String date, @Query("top") int top, @Query("skip") int skip);

    @GET("prontmedappointment/GetAllSpeciality")
    Call<SpecialityListResponse> prontmedGetAllSpeciality();

    @POST("prontmedappointment/SearchForDoctors")
    Call<DoctorListResponse> prontmedSearchForDoctors(@Body SearchForDoctosBody body);

    @POST("life/familyGroup")
    Call<FamilyGroupListResponse> getFamilyGroup(@Body CPFInBody body);

    @POST("prontmedappointment/ListOfAppointments")
    Call<ListOfAppointmentsResponse> prontmedListOfAppointments(@Body ListOfAppointmentsBody body);

    @POST("Life/DependentsWithPendingAnnualRenewal")
    Call<ListDependentsWithPendingAnnualRenewalResponse> dependentsWithPendingAnnualRenewal(@Body DependentsWithPendingAnnualRenewalBody body);

    @POST("Life/AnnualDocumentsByDependent")
    Call<AnnualDocumentsByDependentResponse> annualDocumentsByDependent(@Body CPFInBody body);

    @POST("Life/GetMajorDependent")
    Call<MajorDependentsForAnnualDocumentRenewalResponse> majorDependentsForAnnualDocumentRenewal(@Body CPFInBody body);

    @POST("documents/typesWithoutDocuments")
    Call<TypesWithoutDocumentsResponse> typesWithoutDocuments(@Body TypesWithoutDocumentsBody body);

    @POST("life/SendAnnualRenewalDocumentsForApproval")
    Call<SendAnnualRenewalDocumentsForApprovalResponse> sendAnnualRenewalDocumentsForApproval(@Body SendAnnualRenewalDocumentsForApprovalRequest body);

    @POST("user/getTokenValidationStatus")
    Call<JsonElement> validateTokenStatus(@Body CPFInBody body);

    @POST("user/CreateTokenToAccess")
    Call<JsonElement> createTokenRequest(@Body ValidateTokenRequest body);

    @GET("user/validateToken")
    Call<JsonElement> validateToken(@Query("token") String token);

    @POST("EinsteinAppointment/CreateDriving")
    Call<JsonElement> CreateDriving(@Body UrlTeleHealthRequest body);

    @POST("/Notification/GetPermitions")
    Call<NotificationResponse> GetPermitions(@Body CPFInBody body);

    @POST("/Notification/SavePermitions/")
    Call<JsonElement> savePermitions(@Body NotificationAnswerResponse notificationAnswerResponse);

    @GET("/TermsGeneral/GetPrivacyGeneral/")
    Call<PrivacyTerms> getPrivacyGeneral();

    @GET("/TermsGeneral/GetPrivacyPolicy/")
    Call<PolicyResponse> getPrivacyPolicy();

    @GET("/TermsGeneral/GetDataUsagePolicy/")
    Call<PolicyResponse> getDataUsagePolicy();

    @POST("/terms/getallbyuser/")
    Call<TermOfService> getallbyuser(@Body CPFInBody body);

    @GET("/TermsGeneral/TransparencyPanel")
    Call<JsonElement> getTransparencyPanel(@Query("cpf") String cpf);

    @GET("/TermsGeneral/PrivacyControlText/")
    Call<PrivacyControlResponse> getPrivacyControlText();

    @POST("/termsGeneral/AcceptPanel/")
    Call<JsonElement> saveAcceptPanel(@Body List<AcceptData> acceptDataList);

    @POST("/PoliciesAndTerms/PendingPoliciesAndTermsModal/")
    Call<JsonElement> getPendingPolicies(@Body CheckPlatform body);

    @GET("/FeatureFlag/getmobile/")
    Call<JsonElement> getFeatureFlagLgpd(@Query("name") String name);

    @POST("/life/getfilterdependents/")
    Call<DependentHolder> getfilterdependents(@Body DependentFilterBody body);

    @POST("/terms/getTerms")
    Call<JsonElement> getListOfTerms(@Body GetListOfTerms getListOfTerms);

    @GET("/dental/message/")
    Call<CommitResponse> checkIfBenefitIsAvailable();

    @POST("/MedicalRecords/ConsultationsPerformed")
    Call<ConsultationsPerformed> getListOfConsultationsPerformed(@Body CPFInBody body);

    @POST("/MedicalRecords/ConsultationPerformedDetails")
    Call<JsonElement> getConsultationsPerformedDetails(@Body MedicalRecordsDetails body);

    @POST("/MedicalRecords/DiagnosedDiseases")
    Call<JsonElement> getDiagnosedDiseases(@Body CPFInBody body);

    @POST("/MedicalRecords/MedicalTreatment")
    Call<JsonElement> getMedicalTreatment(@Body CPFInBody body);

    @POST("/MedicalRecords/Allergies")
    Call<JsonElement> getAllergies(@Body CPFInBody body);

    @POST("/MedicalRecords/FamilyHistory")
    Call<JsonElement> getFamilyHistory(@Body CPFInBody body);

    @POST("/MedicalRecords/Surgeries")
    Call<JsonElement> getSurgeries(@Body CPFInBody body);

}
