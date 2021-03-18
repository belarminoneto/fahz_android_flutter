package br.com.avanade.fahz.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.com.avanade.fahz.LogUtils;
import br.com.avanade.fahz.R;
import br.com.avanade.fahz.model.menu.MenuHeader;
import br.com.avanade.fahz.model.menu.MenuItem;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<MenuHeader> mListDataHeader;
    private HashMap<MenuHeader, List<MenuItem>> mListDataChild;

    public ExpandableListAdapter(Context context, List<MenuHeader> listDataHeader, HashMap<MenuHeader, List<MenuItem>> listDataChild) {
        this.context = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listDataChild;
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if(this.mListDataChild!= null && this.mListDataChild.get(this.mListDataHeader.get(groupPosition))!= null) {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        }
        else
            return 0;

    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition).name;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(this.mListDataChild!=null)
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
        else
            return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        final String groupText = (String) getGroup(groupPosition);
        if (convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.menu_group_layout, null);
        }

        TextView txtGroup = convertView.findViewById(R.id.text_list_group);
        TextView txtQtd = convertView.findViewById(R.id.txtQtd);
        TextView txtNew = convertView.findViewById(R.id.txtNew);
        ImageView imgArrow = convertView.findViewById(R.id.arrow_down);
        txtGroup.setText(groupText);
        txtNew.setVisibility(View.INVISIBLE);
        txtQtd.setVisibility(View.INVISIBLE);

        if (groupText.equals("PÁGINA INICIAL")) {
            View rectangle = convertView.findViewById(R.id.rectangle_new);
            rectangle.setVisibility(View.VISIBLE);
        }
//        else if (groupText.equals("BOLSA DE ESTUDO")) {
//            //TODO:  Criar Menu como Tabela?Falar com Thiago
//            txtNew.setVisibility(View.VISIBLE);
//            txtNew.setText("NOVO");
//        }
//        else
//        {
//            txtQtd.setVisibility(View.INVISIBLE);
//        }

        if(this.mListDataChild!= null && this.mListDataChild.get(this.mListDataHeader.get(groupPosition))!= null&&
                this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size()> 0) {

            imgArrow.setVisibility(View.VISIBLE);
        }
        else
        {
            imgArrow.setVisibility(View.INVISIBLE);
        }

        if(groupText.equals("SAIR"))
        {
            txtGroup.setTypeface(null, Typeface.BOLD);
            LinearLayout menuGroupLinear = convertView.findViewById(R.id.menuGroupLinear);
            menuGroupLinear.setBackgroundResource(R.color.menu_dark);
        }
        else
        {
            txtGroup.setTypeface(null, Typeface.NORMAL);
            LinearLayout menuGroupLinear = convertView.findViewById(R.id.menuGroupLinear);
            menuGroupLinear.setBackgroundResource(R.color.menu);
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final MenuItem child = (MenuItem) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.menu_item_layout, parent,false);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.menu_light));
            convertView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        TextView txtListChild = convertView.findViewById(R.id.text_list_item);
        txtListChild.setText(child.name);
        if (!child.canView)
            return null;

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        LogUtils.info("MainActivity", "isChildSelectable - groupPosition: " + groupPosition
                + " childPosition: " + childPosition);
        return groupPosition != 0;
    }

    public void hideItemMenu(int groupId, int childId) {
        MenuItem item = (MenuItem) getChild(groupId, childId);
        item.canView = false;
        this.mListDataChild.get(this.mListDataHeader.get(groupId)).set(childId, item);
        notifyDataSetChanged();
    }
}