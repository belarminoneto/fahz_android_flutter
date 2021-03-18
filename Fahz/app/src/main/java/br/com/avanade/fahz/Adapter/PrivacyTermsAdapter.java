package br.com.avanade.fahz.Adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


 public class PrivacyTermsAdapter extends FragmentPagerAdapter {

    private List<Fragment> listFragments = new ArrayList<>();
    private List<String> listFragmentsTitle =  new ArrayList<>();

    public PrivacyTermsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(Fragment fragment, String title) {
        this.listFragments.add(fragment);
        this.listFragmentsTitle.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listFragmentsTitle.get(position);
    }
}
