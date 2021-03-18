package br.com.avanade.fahz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.OnLifeStatusClickListener;
import br.com.avanade.fahz.model.anamnesisModel.LifeStatusAnamnesis;
import br.com.avanade.fahz.model.anamnesisModel.SectionLifeStatusAnamnesis;

public class FamilyTreeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;
    private final int SECTION = 2;

    private List<SectionLifeStatusAnamnesis> mSectionList;
    private final OnLifeStatusClickListener mOnClickListener;
    private Context mContext;

    public FamilyTreeAdapter(OnLifeStatusClickListener mOnClickListener, Context mContext) {
        this.mOnClickListener = mOnClickListener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;

        switch (viewType) {
            case HORIZONTAL:
                itemView = inflater.inflate(R.layout.horizontal_item_family_tree, parent, false);
                return new ViewHolderHorizontal(itemView);
            case SECTION:
                itemView = inflater.inflate(R.layout.section_item_family_tree, parent, false);
                return new ViewHolderSection(itemView);
            default:
                itemView = inflater.inflate(R.layout.item_family_tree, parent, false);
                return new ViewHolderVertical(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (int i = 0; i < mSectionList.size(); i++) {
            int sectionSize;
//            if (mSectionList.get(i).getPosition() == SectionPosition.HORIZONTAL) {
//                sectionSize = 1;
//            } else {
                sectionSize = mSectionList.get(i).getStatusAnamnesisList().size();
//            }
            if (position == count) {
                return SECTION;
            } else if (position > count && position <= (sectionSize + count)) {

//                if (mSectionList.get(i).getPosition() == SectionPosition.HORIZONTAL) {
//                    return HORIZONTAL;
//                }
                return VERTICAL;
            }
            count += sectionSize + 1;
        }
        return VERTICAL;
    }

    private SectionLifeStatusAnamnesis getSection(int position) {
        int count = 0;
        for (int i = 0; i < mSectionList.size(); i++) {
            int sectionSize;
//            if (mSectionList.get(i).getPosition() == SectionPosition.HORIZONTAL) {
//                sectionSize = 1;
//            } else {
                sectionSize = mSectionList.get(i).getStatusAnamnesisList().size();
//            }
            if (position >= count && position <= (sectionSize + count)) {
                return mSectionList.get(i);
            }
            count += sectionSize + 1;
        }
        return mSectionList.get(0);
    }

    private LifeStatusAnamnesis getLifeStatus(int position) {
        int count = 0;
        for (int i = 0; i < mSectionList.size(); i++) {
            int sectionSize;
//            if (mSectionList.get(i).getPosition() == SectionPosition.HORIZONTAL) {
//                sectionSize = 1;
//            } else {
                sectionSize = mSectionList.get(i).getStatusAnamnesisList().size();
//            }
            if (position > count && position <= (sectionSize + count)) {
                return mSectionList.get(i).getStatusAnamnesisList().get(position - count - 1);
            }
            count += sectionSize + 1;
        }
        return mSectionList.get(0).getStatusAnamnesisList().get(0);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
//            case HORIZONTAL:
//                bindHorizontalView(holder, position);
//                break;
            case SECTION:
                bindSectionView(holder, position);
                break;
            default:
                bindVerticalView(holder, position);
                break;
        }
    }

    private void bindHorizontalView(final RecyclerView.ViewHolder holder, int position) {
        final List<LifeStatusAnamnesis> familyTreeList = getSection(position).getStatusAnamnesisList();
        ViewHolderHorizontal viewHolderHorizontal = (ViewHolderHorizontal) holder;
        FamilyTreeHorizontalAdapter horizontalAdapter = new FamilyTreeHorizontalAdapter(familyTreeList, mOnClickListener, mContext);
        viewHolderHorizontal.rvHorizontalFamilyTree.setHasFixedSize(true);
        viewHolderHorizontal.rvHorizontalFamilyTree.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        viewHolderHorizontal.rvHorizontalFamilyTree.setAdapter(horizontalAdapter);
    }

    private void bindVerticalView(final RecyclerView.ViewHolder holder, int position) {
        final LifeStatusAnamnesis lifeAnamnesis = getLifeStatus(position);
        ViewHolderVertical viewHolderVertical = (ViewHolderVertical) holder;
        viewHolderVertical.txtTitle.setText(lifeAnamnesis.getTypeQuestionnaireFirstLetterCapitalized());
        viewHolderVertical.txtName.setText(lifeAnamnesis.getNameFirstLetterCapitalized());
        String type = mContext.getString(R.string.type_life, lifeAnamnesis.getTypeFirstLetterCapitalized());
        viewHolderVertical.txtType.setText(type);
        viewHolderVertical.txtStatus.setText(lifeAnamnesis.getStatusPercentage());

        int percentage = (int) lifeAnamnesis.getPercentage();
        viewHolderVertical.seekProgress.setProgress(percentage);

        int btnGoToQuestText;
        if (percentage == 100) {
            btnGoToQuestText = R.string.ver_respostas;
            viewHolderVertical.btnGoToQuest.setAlpha(lifeAnamnesis.getEdit() == 1 ? 1f : .5f);
            viewHolderVertical.btnGoToQuest.setEnabled(lifeAnamnesis.getEdit() == 1);
        } else {
            btnGoToQuestText = R.string.ver_resumo;
        }
        viewHolderVertical.btnGoToAnswers.setText(btnGoToQuestText);

        viewHolderVertical.btnGoToQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.goToQuest(lifeAnamnesis);
            }
        });
        viewHolderVertical.btnGoToAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.goToAnswersList(lifeAnamnesis);
            }
        });
    }

    private void bindSectionView(final RecyclerView.ViewHolder holder, int position) {
        String textSection = getSection(position).getHeaderTitle();
        ViewHolderSection viewHolderSection = (ViewHolderSection) holder;
        viewHolderSection.txtSection.setText(textSection);
    }

    @Override
    public int getItemCount() {
        List<LifeStatusAnamnesis> list;
        int totalCount = 0;
        int sectionCount = mSectionList == null ? 0 : mSectionList.size();
        for (int i = 0; i < sectionCount; i++) {

//            if (mSectionList.get(i).getPosition() != SectionPosition.HORIZONTAL) {
                totalCount += 1;
                list = mSectionList.get(i).getStatusAnamnesisList();
                totalCount += list == null ? 0 : list.size();
//            }
//            totalCount += 1;
        }
        return totalCount;
    }

    public void setItems(List<SectionLifeStatusAnamnesis> mSectionList) {
        this.mSectionList = mSectionList;
        notifyDataSetChanged();
    }

    public static class ViewHolderVertical extends RecyclerView.ViewHolder {
        TextView txtTitle, txtName, txtType, txtStatus;
        Button btnGoToAnswers, btnGoToQuest;
        SeekBar seekProgress;

        ViewHolderVertical(View item) {
            super(item);
            txtTitle = item.findViewById(R.id.txtTitle);
            txtName = item.findViewById(R.id.txtName);
            txtType = item.findViewById(R.id.txtType);
            txtStatus = item.findViewById(R.id.txtStatus);
            seekProgress = item.findViewById(R.id.seekProgress);
            btnGoToAnswers = item.findViewById(R.id.btnGoToAnswers);
            btnGoToQuest = item.findViewById(R.id.btnGoToQuest);
        }
    }

    public static class ViewHolderHorizontal extends RecyclerView.ViewHolder {
        RecyclerView rvHorizontalFamilyTree;

        ViewHolderHorizontal(View item) {
            super(item);
            rvHorizontalFamilyTree = item.findViewById(R.id.rvHorizontalFamilyTree);
        }
    }

    public static class ViewHolderSection extends RecyclerView.ViewHolder {
        TextView txtSection;

        ViewHolderSection(View item) {
            super(item);
            txtSection = item.findViewById(R.id.txtSection);
        }
    }
}
