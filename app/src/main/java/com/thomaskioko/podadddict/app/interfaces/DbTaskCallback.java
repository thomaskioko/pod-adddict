package com.thomaskioko.podadddict.app.interfaces;

import com.thomaskioko.podadddict.app.api.model.Item;

import java.util.List;

/**
 * Movie callback interface methods
 *
 * @author Thomad kioko
 */
public interface DbTaskCallback {

    /**
     * @param resultArrayList {@link Item} A list of episode objects
     */
    void CallbackRequest(List<Item> resultArrayList);
}



