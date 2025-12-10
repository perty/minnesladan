package se.minnesladan.core.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.minnesladan.core.llm.ModelTier;

@Service
@Profile("onprem-chat")
public class ModelTierSelectorOnPrem implements ModelTierSelector {
    @Override
    public ModelTier modelTier() {
        return ModelTier.ON_PREM;
    }
}
