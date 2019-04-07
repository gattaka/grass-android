package cz.gattserver.android.common;

public interface OnSuccessAction<T> {
    void run(T urlTaskClient, URLTaskInfoBundle result);
}