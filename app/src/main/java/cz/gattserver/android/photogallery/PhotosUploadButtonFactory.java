package cz.gattserver.android.photogallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
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
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.URLTaskParamTO;
import cz.gattserver.android.interfaces.PhotoTO;

class PhotosUploadButtonFactory {

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
                                URLMultipartTask<T> uploadTask = new URLMultipartTask<>(context,
                                        new OnSuccessAction<T>() {
                                            @Override
                                            public void run(T urlTaskClient, URLTaskInfoBundle result) {
                                                if (result.isSuccess()) {
                                                    if (result.getResponseCode() == 200) {
                                                        Toast.makeText(urlTaskClient, "Fotky byly úspěšně nahrány", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        new AlertDialog.Builder(context)
                                                                .setTitle("Chyba")
                                                                .setMessage("Fotky se nezdařilo odeslat -- server code: " + result.getResponseCode())
                                                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                                                    }
                                                } else {
                                                    new AlertDialog.Builder(context)
                                                            .setTitle("Chyba")
                                                            .setMessage("Fotky se nezdařilo odeslat: " + result.getError().getLocalizedMessage())
                                                            .setIcon(android.R.drawable.ic_dialog_alert).show();
                                                }
                                            }
                                        });
                                SimpleDateFormat sdf = new SimpleDateFormat("d_M_yyyy_HH_mm");
                                List<String> params = new ArrayList<>();
                                params.add("PG_import_" + sdf.format(new Date()));
                                for (PhotoTO p : choosenPhotos.values()) {
                                    params.add(p.getData());
                                    params.add(new File(p.getData()).getName());
                                }
                                String[] arrParams = new String[params.size()];
                                arrParams = params.toArray(arrParams);
                                uploadTask.execute(new URLTaskParamTO(Config.PG_CREATE_FAST, LoginUtils.getSessionid(context)).setParams(arrParams));
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
            }
        });
        sendBtn.setGravity(Gravity.CENTER);
        sendBtn.setEnabled(false);
    }

    public static <T extends ContextWrapper> Button createUploadButton(final T context, final Map<Integer, PhotoTO> choosenPhotos) {
        Button sendBtn = new Button(context);
        setUploadButtonListener(sendBtn, context, choosenPhotos);
        return sendBtn;
    }
}
