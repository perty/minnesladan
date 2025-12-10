package se.minnesladan.core.service;

import se.minnesladan.core.llm.ModelTier;

public interface ModelTierSelector {
    ModelTier modelTier();
}
