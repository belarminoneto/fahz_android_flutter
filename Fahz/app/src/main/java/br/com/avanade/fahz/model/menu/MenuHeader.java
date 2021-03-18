package br.com.avanade.fahz.model.menu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import br.com.avanade.fahz.activities.MainActivity;
import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.model.lgpdModel.EntitieVisible;
import br.com.avanade.fahz.util.Constants;

public class MenuHeader {

    public int id;
    public String name;
    public static ArrayList<MenuHeader> menuHeaders;

    public MenuHeader()
    {
        ListMenuHeader();
    }

    public  MenuHeader(int id, String name)
    {
        this.id = id;
        this.name = name;
    }
    private ArrayList<MenuHeader> ListMenuHeader()
    {
        menuHeaders = new ArrayList<>();
        menuHeaders.add(new MenuHeader(0, "Página Inicial"));
        menuHeaders.add(new MenuHeader(1, "Meus Dados"));

        if (FahzApplication.getInstance() != null && FahzApplication.getInstance().getFahzClaims() != null &&
                FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated) &&
                !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Extended)) {

            if (FahzApplication.getInstance() != null && FahzApplication.getInstance().getFahzClaims() != null &&
                    FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership) &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Extended) &&
                    !FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.INACTIVE_STATUS)
            )
                menuHeaders.add(new MenuHeader(2, "Benefícios Saúde"));
            if (!FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role) &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Extended) &&
                    !FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.INACTIVE_STATUS)
            )
                menuHeaders.add(new MenuHeader(3, "Benefícios Sociais"));
        }

        if (MainActivity.showLgpd &&
                !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated)) {
            //Trata visualização do menus de acordo com a LGPD
            String entities = FahzApplication.getInstance().getFahzClaims().getEntitiesVisible();

            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(entities, JsonElement.class);

            for (int i = 0; i < ((JsonArray) jsonElement).size(); i++) {

                EntitieVisible entitieVisible = gson.fromJson(((JsonArray) jsonElement).get(i), EntitieVisible.class);

                //Boolean.parseBoolean(String.valueOf(entitieVisible.canView))
                if (entitieVisible.idMenu == Constants.MENU_GESTAO_FAHZ_PRIVACIDADE_TERMOS && (entitieVisible.canView == 1)) {
                    menuHeaders.add(new MenuHeader(6, "Privacidade e Termos"));
                }
            }
        } else {
            if (!FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated))
                menuHeaders.add(new MenuHeader(4, "Termos de Uso"));
        }
        menuHeaders.add(new MenuHeader(5,"Sair"));

        return menuHeaders;
    }

    public String getMenuName(int position)
    {
        return menuHeaders.get(position).name;
    }
    public MenuHeader getMenu(int position)
    {
        return menuHeaders.get(position);
    }

}
