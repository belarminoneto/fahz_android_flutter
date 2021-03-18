package br.com.avanade.fahz.util;

public class Constants {
    public static final String MASK_CPF = "###.###.###-##";
    public static final String MASK_DATE = "##/##/####";
    public static final String MASK_HEIGHT = "#,##";
    public static final String MASK_WEIGHT_1D = "#,###";
    public static final String MASK_WEIGHT_2D = "##,###";
    public static final String MASK_WEIGHT_3D = "###,###";

    // public static final String BASE_URL = "http://localhost:63041/";
    public static final long MAX_IMAGE_SIZE = 2;
    public static final String DEFAULT_CPF = "00000000000";
    public static final String LIFE_NAME = "life_name";
    public static final String LIFE_CPF = "life_cpf";
    public static final String LIFE_REFERENCE_YEAR = "reference_year";
    public static final int REQUEST_ADD_ANNUAL_RENEWAL_DOCUMENTS_ACTIVITY = 982;


    // CÓDIGO DE STATUS DA CONSULTA DA SERPRO
    public static String REGULAR = "0";

    public static final int TYPE_FAILURE = 0;
    public static final int TYPE_SUCCESS = 1;

    public static final String ENVIRONMENTDEV = "DEV";
    public static final String ENVIRONMENTQA = "QA";
    public static final String ENVIRONMENTPRD = "ENVIRONMENTPRD";

    ///CONTANTES DE EXTRA INTENT
    public static final String ORIGIN_CALL_DOCUMENT = "origincalldocument";
    public static final String CONTEXT_DOCUMENT = "contextdocument";
    public static final String CONTEXT_DOCUMENT_CPF = "contextdocumentcpf";
    public static final String CONTEXT_DOCUMENT_PLAN = "contextdocumentplan";
    public static final String CONTEXT_REASON_CANCEL_BENEFIT = "contextdocumentidbenefit";
    public static final String HAS_CHANGE_PASSWORD = "haschangepassword";
    public static final String BENEFITIS_CONTROL = "benefitscontrol";
    public static final String CPF_EDIT_DEPENDENT = "cpfeditdependent";
    public static final String VIEW_EDIT_DEPENDENT = "vieweditdependet";
    public static final String SUCCESS_MESSAGE_INSERT_DEPENDENT = "successmessageinsertdependent";
    public static final String IS_REACTIVATE = "isreactivate";
    public static final String ADHESION_HEALTH_CONTROL = "adehesionhealthcontrol";
    public static final String BENEFIT_EXTRA = "benefitextra";
    public static final String BENEFIT_DESCRIPTION_EXTRA = "benefitdescription";
    public static final String BASE_HEALTH_CONTROL = "basehealthcontrol";
    public static final String BASE_MESSAGE_CONTROL = "basemessagecontrol";
    public static final String IDPLAN_SCHOOL_SUPPLIES = "idplanschoolsupples";
    public static final String SCHOOL_SUPPLIES_REFUND = "schoolsupplesrefund";
    public static final String ALREADY_HAS_PLAN = "alreadyhasplan";
    public static final String ALREADY_HAS_REFUND = "alreadyhasrefund";
    public static final String HAS_BANK_DATA = "hasbankdata";
    public static final String CAN_SEARCH_PICTURE = "cansearchpicture";
    public static final String BITMAP_PROFILE = "bitmapprofile";
    public static final String BASE_SCHOOL_CONTROL = "schoolcontrol";
    public static final String PLAN_ID = "planid";
    public static final String NAME_TO_DOCUMENTO_SCHOOL_SUPPLIES = "nametodocumentoschool";
    public static final String REQUEST_ACCOUNT_DOCUMENT = "requestaccountdocument";
    public static final String INSERT_DEPENDENT_OBJECT = "insertdependentobject";
    public static final String BANK_FOR_REFUND = "bankforrefund";
    public static final String FROM_CARD = "from";
    public static final String SCHOLARSHIP_CONTROL = "scholarshipcontrol";
    public static final String SCHOLARSHIP_REFUND_DOCUMENT = "scholarshiprefunddocument";
    public static final String SCHOLARSHIP_ID = "scholarshipid";
    public static final String CAN_SEND_DOCUMENT = "cansenddocument";
    public static final String DOCUMENTS_SAVED = "documentsSaved";
    public static final String DOCUMENT_REFUND_SAVED_LOCAL = "documentsSaved";
    public static final String SHOW_WARNING_VERSION = "showwarningversion";
    public static final String JSON_VALIDATE_TOKEN = "jsonvalidatetoken";
    public static final String FIRST_ACCESS_TOKEN = "firstAccess";
    public static final String PUSH_NOTIFICATION_ACTIVITY = "pushnotificationactivity";

    public static final String ADHESION_DENTAL_CONTROL = "adehesiondentalhcontrol";
    public static final String ADHESION_PHARMACY_CONTROL = "adehesionpharmacyhcontrol";
    public static final String ADHESION_HIDE_BANK = "adehesionshowbank";

    public static final String HISTORY_ID = "historyId";

    ///CONTANTES START ACTIVITY FOR RESULT
    public static final int REQUEST_CAMERA_PERMISSION = 1;
    public static final int REQUEST_STORAGE_PERMISSION = 2;
    public static final int REQUEST_CAMERA_CAPTURE = 3;
    public static final int REQUEST_PICTURE_GALLERY = 4;
    public static final int DOCUMENT_FOR_RESULT = 5;
    public static final int TERMS_OF_USE_RESULT = 6;
    public static final int DEPENDENT_RESULT = 7;
    public static final int TERMS_OF_USE_RESULT_REFUND = 8;
    public static final int TERMS_OF_USE_RESULT_CARD = 9;
    public static final int TERMS_OF_USE_RESULT_WORKING = 10;
    public static final int TERMS_OF_USE_RESULT_SHARED = 11;
    public static final int SCHOOL_SUPPLIES_VALIDADE_PROFILE_CARD = 12;
    public static final int SCHOOL_SUPPLIES_VALIDADE_PROFILE_REFUND = 13;
    public static final int GALLERY_INTENT_CALLED = 14;
    public static final int GALLERY_KITKAT_INTENT_CALLED = 15;
    public static final int INFORMATIVE_TOKEN_RESULT = 16;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 17;
    public static final int REQUEST_MODIFY_AUDIO_SETTINGS_PERMISSION = 18;

    //CONSTANTES DE BENEFICIOS
    public static final int BENEFITHEALTH = 1;
    public static final int BENEFITDENTAL = 2;
    public static final int BENEFITPHARMA = 3;

    //CONSTANTES DESCRICAO BENEFICIOS
    public static final String BENEFITHEALTH_DESC = "Plano Médico";
    public static final String BENEFITDENTAL_DESC = "Plano Odonto";
    public static final String BENEFITPHARMA_DESC = "Plano Farmácia";

    //CONSTANTE TERMO DE ACEITE
    public static final String TERMS_OF_USE_CODE_GENERAL = "ACEITE_GERAL";
    public static final String TERMS_OF_USE_CODE_GENERAL_DEP = "ACEITE_GERAL_DEPENDENTE";
    public static final String TERMS_OF_INACTIVATE_DEPENDENTS = "TERMO_INATIVACAO_DEPENDENTES";
    public static final String TERMS_OF_ACTIVATION_PLAN_ODONTO_MINOR = "TERMO_ADESAO_ODONTO_MENOR";
    public static final String TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR = "TERMO_ADESAO_SAUDE_MENOR";
    public static final String TERMS_OF_ACTIVATION_PLAN_PHARMACY_MINOR = "TERMO_ADESAO_FARMACIA_MENOR";
    public static final String TERMS_OF_ACTIVATION_PLAN_ODONTO_MAJOR = "ADESAO_ODONTO_FILHO_MAIOR_SEM_CART_TRAB";
    public static final String TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR = "ADESAO_SAUDE_FILHO_MAIOR_SEM_CART_TRAB";
    public static final String TERMS_OF_ACTIVATION_PLAN_HEALTH_SON_SHARED = "ADESAO_SAUDE_FILHO_GUARDA_COMPARTILHADA";
    public static final String TERMS_OF_ACTIVATION_PLAN_ODONTO_SON_SHARED = "ADESAO_ODONTO_FILHO_GUARDA_COMPARTILHADA";
    public static final String TERMS_OF_USE_CODE_HEALTH = "ADESAO_SAUDE";
    public static final String TERMS_OF_USE_CODE_WHATSAPP = "TERMO_USO_WHATSAPP";
    public static final String TERMS_OF_USE_CODE_PHARMACY = "ADESAO_FARMACIA";
    public static final String TERMS_OF_USE_CODE_DENTAL = "ADESAO_ODONTO";
    public static final String TERMS_OF_USE_CODE_EXCLUSION_DENTAL = "TERMO_EXCLUSAO_PLANO_ODONTO";
    public static final String TERMS_OF_USE_CODE_EXCLUSION_HEALTH = "TERMO_EXCLUSAO_PLANO_SAUDE";
    public static final String TERMS_OF_USE_CODE_EXCLUSION_PHARMA = "TERMO_EXCLUSAO_PLANO_FARMACIA";
    public static final String TERMS_OF_USE_SELECTED = "termsofuseselected";
    public static final String TERMS_OF_USE_CPF = "termsofusecpf";
    public static final String TERMS_OF_USE_FROM_BENEFIT = "termsofusefrombenefit";
    public static final String TERMS_OF_USE_FROM_REGISTER = "termsofusefromregister";
    public static final String TERMS_OF_USE_FROM_FEATURE = "termsofusefromfeature";
    public static final String ADESAO_PLANO_MATERIAL_ESCOLAR = "ADESAO_PLANO_MATERIAL_ESCOLAR";
    public static final String TERMO_ORIENTACAO_2VIA_CARTEIRA_BRA = "TERMO_ORIENTACAO_2VIA_CARTEIRA_BRA";
    public static final String TERMO_ORIENTACAO_2VIA_CARTEIRA_SUL = "TERMO_ORIENTACAO_2VIA_CARTEIRA_SUL";
    public static final String TERMO_ORIENTACAO_2VIA_CARTEIRA_ODONTO = "TERMO_ORIENTACAO_2VIA_CARTEIRA_ODONTO";
    public static final String INCLUSAO_FILHO_GUARDA_COMPARTILHADA = "INCLUSAO_FILHO_GUARDA_COMPARTILHADA";
    public static final String INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB = "INCLUSAO_FILHO_MAIOR_SEM_CART_TRAB";


    //CONSTANTES PARA LOGAR
    public static final String  ERROR_UNAUTHORIZED = "401";
    public static final String  ERROR_DESCRIPTION_UNAUTHORIZED = "Acesso inválido. Faça o login novamente.";

    //CONSTANTES CONSULTA CIDADE
    public static final int START_SHOW_AUTO_COMPLETE = 2;
    public static final int START_SEARCH_AUTO_COMPLETE = 1;

    //TIPOS DE GRAU DE PARENTESCO
    public static final String SPOUSE = "1";
    public static final String COMPANION = "2";
    public static final String SON = "3";
    public static final String TUTOR = "4";
    public static final String CURATOR = "5";
    public static final String GUARD = "6";
    public static final String STEPSON = "7";
    public static final String FATHER = "8";
    public static final String MOTHER = "9";

    //DATA ADMISSION FOR STUDENT
    public static final String LAST_ADMISSION_DATE = "31/07/2001";

    //TIPOS DE GRAU DE ESTADO CIVIL
    public static final int SINGLE = 0;

    //Status Vida
    public static final String PENDING_STATUS = "0";
    public static final String ACTIVE_STATUS = "1";
    public static final String INACTIVE_STATUS = "3";

    //MENSAGENS
    public static final int NUMBER_OF_SHOWN_MESSAGES =10;

    //REQUESTS
    public static final int NUMBER_OF_SHOWN_REQUESTS =10;


    //Enum com os tipos de origem (T_ORIGEM_VIDA)
    public static final int Brahma = 1;
    public static final int Antarctica = 2;
    public static final int Ambev = 3;
    public static final int Fahz = 4;
    public static final int Skol = 5;
    public static final int ORIGEM_12 = 12;
    public static final int ORIGEM_21 = 21;


    //TIPOS DOCUMENTO

    public static final int CNH = 1;
    public static final int Comprovante_de_Endereco = 2;
    public static final int Carteira_Profissional = 3;
    public static final int Titulo_de_Eleitor = 4;
    public static final int Contra_Cheque = 5;
    public static final int Certidao_de_Casamento = 6;
    public static final int RG = 7;
    public static final int Averbacaoo_de_Separacao_Judicial = 7;
    public static final int Declaracao_de_Adocao = 9;
    public static final int Documento_Oficial_de_Tutela = 10;
    public static final int Documento_Oficial_de_Curatela = 11;
    public static final int Documento_Oficial_de_Guarda = 12;
    public static final int Declaracao_de_Invalidez = 13;
    public static final int Declaracao_de_Matricula_em_Curso_Superior = 14;
    public static final int CPF = 15;
    public static final int Declaracao_de_Uniao_Estavel = 16;
    public static final int Nota_Fiscal_ou_Cupom_Fiscal = 17;
    public static final int Comprovante_de_Conta_Corrente = 18;
    public static final int Comprovante_Escolar = 19;

    public static final String Nota_Fiscal_ou_Cupom_Fiscal_str = "Nota Fiscal / Cupom Fiscal";

    public static final int SNACK_DURATION = 12000;

    //CONSTANTES DE STATUS REEMBOLSO
    public static final int PENDING_REFUND = 1;

    //OPERADORAS
    public static final int CNU = 1;
    public static final int BRADESCO = 3;
    public static final int SUL = 2;

    //MOTIVOS DESLIGAMENTO
    public static final int Possui_Outro_Plano = 1;
    public static final int Obito = 2;
    public static final int Nao_reconhecimento_de_paternidade = 3;
    public static final int Matrimonio_do_dependente = 4;
    public static final int Separacao_da_Mae_do_Enteado = 5;
    public static final int Execício_de_atividade_remunerada = 6;
    public static final int Nao_e_Estudante = 7;
    public static final int Separacao_Companheiro = 8;
    public static final int Separacao_judicial_ou_Divorcio = 9;

    //Organization Attribution
    public static final String Intership = "Estágio";
    public static final String Reintegrated = "Reintegrado";
    public static final String Extended = "Prorrogado";
    public static final String Retired = "Aposentado";
    public static final String Normal = "Normal";

    //CESTA DE NATAL
    public static final int typeAddress = 1;
    public static final int typeWorkspace = 2;

    //ACESSO DEPENDENTE AO SISTEMA
    public static final String Dependent_Role = "Dependente";
    public static final int PRONTMED_DEFAULT_PAGINATION_ITENS = 20;

    //STATUS DE TOKEN
    public static final int TOKEN_NEVER_DID = 0;
    public static final int TOKEN_SUCESS = 1;
    public static final int TOKEN_ON_FIVE_MINUTES = 2;
    public static final int TOKEN_VALID_OUT_FIVE_MINUTES = 3;

    //TELAS ABERTOS PELO PUSH NOTIFICATION
    public static final String LIST_MESSAGE_ACTIVITY = "listmessage";

    //DADOS ENVIADOS PELO PUSH NOTIFICATION
    public static final String TITLE_DATA = "Title";
    public static final String BODY_DATA = "Body";

    //KEY PAINEL LGPD
    public static final String API_KEY_LGPD = "e4c8903c-7b1a-4e4b-bfa9-92a09c1250d1";

    public static final int  TERM_ACCEPT = 1;
    public static final int  TERM_READ = 2;
    public static final int  TERM_DECLINED = 3;
    public static final int  TERM_REVOKE = 4;

    public static final int MENU_GESTAO_FAHZ_PRIVACIDADE_TERMOS = 58;
    public static final int BOTAO_GESTAO_FAHZ_CONTROLE_PRIVACIDADE = 59;
    public static final int BOTAO_GESTAO_FAHZ_CONTROLE_TERMOS = 60;
    public static final int MENU_GESTAO_FAHZ_MENU_PERFIL_DADOS = 61;
    public static final int MENU_GESTAO_FAHZ_MENU_PERFIL_ENDERECO = 62;
    public static final int MENU_GESTAO_FAHZ_MENU_PERFIL_BANCARIOS = 63;
    public static final int MENU_GESTAO_FAHZ_MENU_PERFIL_DOCUMENTO = 64;
    public static final int MENU_GESTAO_FAHZ_MENU_PERFIL_CORPORATIVO = 65;
    public static final int MENU_GESTAO_FAHZ_MENU_PERFIL_AUXILIARES = 66;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_DADOS = 67;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_DOCUMENTOS = 68;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_ADICIONAR = 69;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_EDITAR = 70;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_INATIVAR = 71;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_VISUALIZAR = 72;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_REATIVAR = 73;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_SAUDE_INCLUSAO = 74;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_SAUDE_INATIVACAO = 75;
    public static final int MENU_GESTAO_FAHZ_MENU_DEPENDENTES_ODONTO_INCLUSAO = 76;

    //Links LGPD
    public static final String LINK_CONTROLES_DE_POLITICA_DE_PRIVACIDADE = "Acessar Controles Política de Privacidade";
    public static final String LINK_CONTROLES_DE_TERMOS_DE_SERVICO = "Acessar Controles Termos de Serviço";
    public static final String LINK_CENTRAL_DE_NOTIFICACOES = "Acessar Central de Notificações";

    //Tipo Politicas
    public static final int REQUEST_CODE_PRIVACY_POLICE = 1;
    public static final int REQUEST_CODE_DATE_USE = 2;
    public static final int PRIVACY_POLICE_END_DATE_USE = 1;

    public static final String FEATURE_FLAG_PLATFORM = "LGPD_ANDROID";

    //Tipo de documentos para aceite
    public static final int LIST_OF_TERMS = 1;
    public static final int LIST_OF_POLICIES = 2;

    //Tipo de Acoes caso negado o termo/politica - TermActionAfterNegativeAccept
    public static final int LOG_OFF = 1;
    public static final int HOME = 2;
    public static final int CANCEL_BENEFIT = 3;

}
