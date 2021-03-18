package br.com.avanade.fahz.interfaces;

import br.com.avanade.fahz.model.anamnesisModel.LifeStatusAnamnesis;

public interface OnLifeStatusClickListener {
    void goToQuest(LifeStatusAnamnesis lifeStatus);
    void goToAnswersList(LifeStatusAnamnesis lifeStatus);
}