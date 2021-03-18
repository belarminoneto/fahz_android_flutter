package br.com.avanade.fahz.model.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.util.Constants;

public class MenuItem {

    public int id;
    public String name;
    public boolean canView;

    public MenuItem(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.canView = true;
    }

    public static HashMap<MenuHeader, List<MenuItem>> ListMenuItem(boolean canViewSchoolSupplies, boolean canViewDocumentsWithPendingRenewal, boolean mCanViewDocuments)
    {
        HashMap<MenuHeader, List<MenuItem>>  menuItems = new HashMap<>();
        MenuHeader header = new MenuHeader();

        //LISTA DE MENU MEUS DADOS
        List<MenuItem> itensData = new ArrayList<>();
        itensData.add(new MenuItem(0, "Meu Perfil"));
        if(FahzApplication.getInstance() != null &&
                FahzApplication.getInstance().getFahzClaims()!= null) {

            if (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    FahzApplication.getInstance().getFahzClaims().getRoles() != null &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership) &&
                    !FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role))
                itensData.add(new MenuItem(1, "Dependentes"));

            if (mCanViewDocuments)
                itensData.add(new MenuItem(2, "Documentos"));

            if (canViewDocumentsWithPendingRenewal)
                itensData.add(new MenuItem(3, "Documentos de Renovação anual"));
            itensData.add(new MenuItem(4, "Alterar Senha"));
            menuItems.put(header.getMenu(1), itensData);

            //Exibir somente para dependente maior
            if (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    FahzApplication.getInstance().getFahzClaims().getRoles() != null &&
                    (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Normal) ||
                     FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Retired)) &&
                    FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role) &&
                    !FahzApplication.getInstance().getFahzClaims().getLifeStatus().contains(Constants.INACTIVE_STATUS)
            ) {
                itensData.add(new MenuItem(5, "Inativar Cadastro"));
            }


            if (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Reintegrated) &&
                    !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Extended) &&
                    !FahzApplication.getInstance().getFahzClaims().getLifeStatus().equals(Constants.INACTIVE_STATUS)) {

                //LISTA DE MENU BENEFICIOS SAUDE
                if (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                        !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership)) {
                    List<MenuItem> itensHealth = new ArrayList<>();
                    itensHealth.add(new MenuItem(0, "Plano de Saúde"));
                    itensHealth.add(new MenuItem(1, "Plano Odonto"));
                    //Validar se origem fahz para mostrar Faramacia
                    if (FahzApplication.getInstance().getFahzClaims().getSource() != null &&
                            FahzApplication.getInstance().getFahzClaims().getSource().equals(Integer.toString(Constants.Fahz)) &&
                            !FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
                        itensHealth.add(new MenuItem(2, "Farmácia"));
                    }
                    menuItems.put(header.getMenu(2), itensHealth);
                }

                //LISTA DE MENU BENEFICIOS SOCIAIS
                if (FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution() != null &&
                        !FahzApplication.getInstance().getFahzClaims().getOrganizationalAttribution().equals(Constants.Intership) &&
                        !FahzApplication.getInstance().getFahzClaims().getRoles().contains(Constants.Dependent_Role)) {
                    List<MenuItem> itensSocial = new ArrayList<>();
                    itensSocial.add(new MenuItem(0, "Bolsa de Estudos"));
                    itensSocial.add(new MenuItem(1, "Brinquedo"));
                    itensSocial.add(new MenuItem(2, "Cesta de Natal"));
                    if (canViewSchoolSupplies)
                        itensSocial.add(new MenuItem(3, "Material Escolar"));
                    menuItems.put(header.getMenu(3), itensSocial);
                } else if (canViewSchoolSupplies) {
                    List<MenuItem> itensSocial = new ArrayList<>();
                    itensSocial.add(new MenuItem(3, "Material Escolar"));
                    menuItems.put(header.getMenu(2), itensSocial);
                }
            }
        }

        return menuItems;
    }

}
