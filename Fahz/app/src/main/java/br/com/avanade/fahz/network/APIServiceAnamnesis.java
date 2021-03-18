package br.com.avanade.fahz.network;

import java.util.List;

import br.com.avanade.fahz.model.anamnesisModel.AnswerQuestionnaireRequest;
import br.com.avanade.fahz.model.anamnesisModel.LifeAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.LifeStatusAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.LoginAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireRequest;
import br.com.avanade.fahz.model.anamnesisModel.SearchAuxiliaryDataRequest;
import br.com.avanade.fahz.model.anamnesisModel.SearchFamilyTreeRequest;
import br.com.avanade.fahz.model.anamnesisModel.SearchLifeRequest;
import br.com.avanade.fahz.model.anamnesisModel.AnswerQuestionnaireResponse;
import br.com.avanade.fahz.model.anamnesisModel.LoginAnamnesisResponse;
import br.com.avanade.fahz.model.anamnesisModel.QuestionnaireResponse;
import br.com.avanade.fahz.model.anamnesisModel.SearchAuxiliaryDataResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIServiceAnamnesis {
    @POST("Logon")
    Call<LoginAnamnesisResponse> loginWithPendencies(@Body LoginAnamnesis user);

    @POST("login")
    Call<LoginAnamnesisResponse> loginWithoutPendencies(@Body LoginAnamnesis user);

    @POST("PesquisarArvoreFamiliarPorCpf")
    Call<List<LifeStatusAnamnesis>> searchForFamilyTreeByCPF(@Body SearchFamilyTreeRequest request);

    @POST("PesquisarVidaPorFiltro/Post")
    Call<List<LifeAnamnesis>> searchLifeByFilter(@Body SearchLifeRequest request);

    @POST("Questionario/GetParaResponder")
    Call<QuestionnaireResponse> getQuestionnaire(@Body QuestionnaireRequest request);

    @POST("Preenchimento")
    Call<AnswerQuestionnaireResponse> answerQuestionnaire(@Body AnswerQuestionnaireRequest request);

    @POST("PesquisarTabelaAuxiliar ")
    Call<SearchAuxiliaryDataResponse> searchAuxData(@Body SearchAuxiliaryDataRequest request);

    @GET("media_resources/{imageName}")
    Call<ResponseBody> getImage(@Path("imageName") String imageName);
}
