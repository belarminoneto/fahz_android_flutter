package br.com.avanade.fahz.model.dashboard;

import java.util.ArrayList;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.util.Constants;

public class DashboardItem {


    public int nameMenu;
    public int drawableMenu;

    public DashboardItem()
    {
    }

    public DashboardItem(int name, int drawable)
    {
        this.nameMenu = name;
        this.drawableMenu = drawable;
    }

    public static List<DashboardItem> ListDashBoardItems(boolean canViewSchoolSupplies, boolean showLgpd) {
        //LISTA DE ITEMS DO MENU DE DASHBOARD PRINCIPAL
        List<DashboardItem> itensData = new ArrayList<>();

        if (!FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.INACTIVE_STATUS) &&
                !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Extended) &&
                !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated)) {

            if (FahzApplication.getInstance() != null && FahzApplication.getInstance().getFahzClaims() != null &&
                    FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership))
                itensData.add(new DashboardItem(R.string.health_plan, R.drawable.ic_plano_saude));

            if (FahzApplication.getInstance() != null && FahzApplication.getInstance().getFahzClaims() != null &&
                    FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership))
                itensData.add(new DashboardItem(R.string.dental_plan, R.drawable.ic_odonto));

            if (FahzApplication.getInstance() != null && FahzApplication.getInstance().getFahzClaims() != null &&
                    FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    FahzApplication.getInstance().getFahzClaims().getSource() != null
                    && FahzApplication.getInstance().getFahzClaims().getSource().equals(Integer.toString(Constants.Fahz))
                    && !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership)
                    && !FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
                itensData.add(new DashboardItem(R.string.pharmacy, R.drawable.ic_farmacia));
            }


            if (FahzApplication.getInstance() != null && FahzApplication.getInstance().getFahzClaims() != null &&
                    FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership)
                    && !FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role))
                itensData.add(new DashboardItem(R.string.scholarship, R.drawable.ic_bolsa));
            if (canViewSchoolSupplies)
                itensData.add(new DashboardItem(R.string.school_supplies, R.drawable.ic_material_escolar));

            if (FahzApplication.getInstance() != null && FahzApplication.getInstance().getFahzClaims() != null &&
                    FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership)
                    && !FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {

                itensData.add(new DashboardItem(R.string.toy, R.drawable.ic_brinquedo));
                itensData.add(new DashboardItem(R.string.christmas, R.drawable.ic_natal));
            }
            itensData.add(new DashboardItem(R.string.doubts, R.drawable.ic_doubt));
            itensData.add(new DashboardItem(R.string.questionsandanswers, R.drawable.ic_question));

/*
        } else if (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated)) {
            itensData.add(new DashboardItem(R.string.title_privacy_control, R.drawable.ic_control_privacy));
*/
        }

/*
        if (showLgpd && !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated)) {
            //Trata visualização do menus de acordo com a LGPD
            String entities = FahzApplication.getInstance().getFahzClaims().getEntitiesVisible();

            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(entities, JsonElement.class);

            for (int i = 0; i < ((JsonArray) jsonElement).size(); i++) {

                EntitieVisible entitieVisible = gson.fromJson(((JsonArray) jsonElement).get(i), EntitieVisible.class);

                if (entitieVisible.idMenu == Constants.BOTAO_GESTAO_FAHZ_CONTROLE_PRIVACIDADE && (entitieVisible.canView != 0)) {
                    itensData.add(new DashboardItem(R.string.title_privacy_control, R.drawable.ic_control_privacy));
                } else if (entitieVisible.idMenu == Constants.BOTAO_GESTAO_FAHZ_CONTROLE_TERMOS && (entitieVisible.canView != 0)) {
                    itensData.add(new DashboardItem(R.string.title_term_service, R.drawable.ic_control_terms));
                }
            }
        }*/

        return itensData;
    }
}
