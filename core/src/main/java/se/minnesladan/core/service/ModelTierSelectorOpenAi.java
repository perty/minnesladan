package se.minnesladan.core.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.minnesladan.core.llm.ModelTier;

@Service
@Profile("openai-chat")
public class ModelTierSelectorOpenAi implements ModelTierSelector {
    @Override
    public ModelTier modelTier() {
        return ModelTier.HIGH_CAPABILITY;
    }
}
