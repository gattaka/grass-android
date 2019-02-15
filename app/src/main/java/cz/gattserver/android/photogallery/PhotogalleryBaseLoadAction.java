package cz.gattserver.android.photogallery;

import cz.gattserver.android.common.URLTask;

public class PhotogalleryBaseLoadAction implements URLTask.OnSuccessAction<PhotogalleryActivity> {

    @Override
    public void run(PhotogalleryActivity instance, String result) {
        instance.populateBaseInfo(result);
    }
}
