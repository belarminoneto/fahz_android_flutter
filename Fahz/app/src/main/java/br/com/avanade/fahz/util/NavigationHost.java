package br.com.avanade.fahz.util;


import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void navigateTo(Fragment fragment, boolean addToBackStack);
}
