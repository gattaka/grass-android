package cz.gattserver.android.photogallery;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cz.gattserver.android.Config;
import cz.gattserver.android.common.LoginUtils;
import cz.gattserver.android.common.OnSuccessAction;
import cz.gattserver.android.common.URLMultipartTask;
import cz.gattserver.android.common.URLPostTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.URLTaskParamTO;
import cz.gattserver.android.interfaces.PhotoTO;

class PhotosUploadButtonFactory {

    private static class Result {
        boolean success;
        Toast lastToast;

        public Result(boolean success, Toast lastToast) {
            this.success = success;
            this.lastToast = lastToast;
        }

    }

    public static <T extends ContextWrapper> void setUploadButtonListener(Button sendBtn, final T context, final Map<Integer, PhotoTO> choosenPhotos) {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Odeslání fotek")
                        .setMessage("Opravdu odeslat fotky?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                create(context, choosenPhotos);
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
            }
        });
        sendBtn.setGravity(Gravity.CENTER);
        sendBtn.setEnabled(false);
    }

    private static <T extends ContextWrapper> Result checkAndNotify(final T context, URLTaskInfoBundle result, String okMessage, String errorMessage) {
        if (result.isSuccess()) {
            if (result.getResponseCode() == 200) {
                if (okMessage != null) {
                    Toast toast = Toast.makeText(context, okMessage, Toast.LENGTH_SHORT);
                    toast.show();
                    return new Result(true, toast);
                }
                return new Result(true, null);
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Chyba")
                        .setMessage(errorMessage + " -- server code: " + result.getResponseCode())
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("Chyba")
                    .setMessage(errorMessage + ": " + result.getError().getLocalizedMessage())
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        }
        return null;
    }

    private static <T extends ContextWrapper> void create(final T context, final Map<Integer, PhotoTO> choosenPhotos) {
        Log.d("P.UploadButtonFactory", "create");
        URLPostTask<T> task = new URLPostTask<>(context, new OnSuccessAction<T>() {
            @Override
            public void run(T urlTaskClient, URLTaskInfoBundle result) {
                Result checkResult = checkAndNotify(context, result, null, "Nezdařilo se založit galerii");
                if (checkResult.success)
                    upload(context, new ArrayList<>(choosenPhotos.values()), 0, result.getResultAsStringUTF(), checkResult.lastToast);
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_HH_mm");
        task.execute(new URLTaskParamTO(Config.PG_CREATE, LoginUtils.getSessionid(context)).setParams("galleryName", "PG_import_" + sdf.format(new Date())));
    }

    private static <T extends ContextWrapper> void upload(final T context, final List<PhotoTO> photos, final int index, final String galleryId, final Toast lastToast) {
        Log.d("P.UploadButtonFactory", "upload");
        URLMultipartTask<T> uploadTask = new URLMultipartTask<>(context,
                new OnSuccessAction<T>() {
                    @Override
                    public void run(T urlTaskClient, URLTaskInfoBundle result) {
                        if (lastToast != null)
                            lastToast.cancel();
                        Result checkResult = checkAndNotify(context, result, "Zpracována fotka " + (index + 1) + " z " + photos.size(), "Nezdařil se upload fotky");
                        if (checkResult.success)
                            if (index + 1 < photos.size()) {
                                upload(context, photos, index + 1, galleryId, checkResult.lastToast);
                            } else {
                                process(context, galleryId, checkResult.lastToast);
                            }
                    }
                });

        PhotoTO p = photos.get(index);
        uploadTask.execute(new URLTaskParamTO(Config.PG_UPLOAD, LoginUtils.getSessionid(context))
                .setFileParams(p.getData(), new File(p.getData()).getName())
                .setParams("galleryId", galleryId));
    }

    private static <T extends ContextWrapper> void process(final T context, String galleryId, final Toast lastToast) {
        Log.d("P.UploadButtonFactory", "process");
        URLPostTask<T> task = new URLPostTask<T>(context, new OnSuccessAction<T>() {
            @Override
            public void run(T urlTaskClient, URLTaskInfoBundle result) {
                Result checkResult = checkAndNotify(context, result, null, "Nezdařilo se zpracovat galerii");
                if (checkResult.success)
                    new AlertDialog.Builder(context)
                            .setTitle("Nahrání dokončeno")
                            .setMessage("Fotky byly úspěšně nahrány")
                            .setIcon(android.R.drawable.ic_dialog_info).show();
            }
        });
        task.execute(new URLTaskParamTO(Config.PG_PROCESS, LoginUtils.getSessionid(context)).setParams("galleryId", galleryId));
    }

}
