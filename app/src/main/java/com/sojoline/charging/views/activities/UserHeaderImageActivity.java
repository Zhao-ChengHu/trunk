package com.sojoline.charging.views.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sojoline.charging.LvAppUtils;
import com.sojoline.charging.R;
import com.sojoline.charging.utils.GlideLoader;
import com.sojoline.charging.utils.IntentUtils;
import com.sojoline.charging.views.base.LvBaseAppCompatActivity;
import com.sojoline.model.dagger.ApiComponentHolder;
import com.sojoline.model.response.SimpleResponse;
import com.sojoline.model.response.UploadFileResponse;
import com.sojoline.model.storage.AppInfosPreferences;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.leanvision.baseframe.rx.SimpleSubscriber;
import cn.com.leanvision.baseframe.rx.transformers.SchedulersCompat;
import cn.com.leanvision.baseframe.util.LvFileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.functions.Func1;

/********************************
 * Created by lvshicheng on 2017/8/3.
 ********************************/
@Route(path = "/login/mime/header")
@RuntimePermissions
public class UserHeaderImageActivity extends LvBaseAppCompatActivity {

    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.bt_album)
    Button    btAlbum;
    @BindView(R.id.bt_camera)
    Button    btCamera;

    public static void navigation() {
        ARouter.getInstance().build("/login/mime/header").navigation();
    }

    @Override
    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.aty_user_header);
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbarNav("设置个人头像");

        GlideLoader.getInstance().displayImage(getApplicationContext(), AppInfosPreferences.get().getHeaderUrl(), ivHeader);
    }

    @OnClick(R.id.bt_album)
    public void clickFromAlbum() {
        UserHeaderImageActivityPermissionsDispatcher.openAlbumWithPermissionWithCheck(this);
    }

    private Uri    imageUri;
    // 最后剪裁后的路径
    private String headCropPath;

    @OnClick(R.id.bt_camera)
    public void clickFromCamera() {
        UserHeaderImageActivityPermissionsDispatcher.openCameraWithPermissionWithCheck(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    imageUri = data.getData();
                    cropPhoto();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    cropPhoto();
                }
                break;
            case 3:
                if (data != null) {
                    final File file = new File(headCropPath);
                    if (file.exists()) {
                        GlideLoader.getInstance().displayImage(getApplicationContext(), file.getAbsolutePath(), ivHeader);

                        showLoadingDialog("修改头像");
                        /**
                         * 上传服务器代码
                         */
                        Observable.just(file)
                            .flatMap(new Func1<File, Observable<UploadFileResponse>>() {
                                @Override
                                public Observable<UploadFileResponse> call(File file) {
//                                        File fileNew = new Compressor(getApplicationContext()).compressToFile(file);
                                    String fileMD5 = LvFileUtils.getFileMd5_16(file);
                                    MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("md5orsha1", fileMD5 == null ? "" : fileMD5)
                                        .addFormDataPart("upfile", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                                        .build();
                                    return ApiComponentHolder.sApiComponent.apiService().uploadSingleImage(body);
                                }
                            })
                            .flatMap(new Func1<UploadFileResponse, Observable<SimpleResponse>>() {
                                @Override
                                public Observable<SimpleResponse> call(UploadFileResponse uploadFileResponse) {
                                    if (uploadFileResponse.isSuccess()) {
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("imgUrl", uploadFileResponse.content);
                                        AppInfosPreferences.get().setHeaderUrl(uploadFileResponse.content);
                                        return ApiComponentHolder.sApiComponent.apiService().updateHeaderImg(map);
                                    } else {
                                        return Observable.error(new Throwable("文件上传失败"));
                                    }
                                }
                            })
                            .compose(SchedulersCompat.<SimpleResponse>applyNewSchedulers())
                            .subscribe(new SimpleSubscriber<SimpleResponse>() {
                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    dismissLoadingDialog();
                                    showToast("修改失败，请重试");
                                }

                                @Override
                                public void onNext(SimpleResponse response) {
                                    dismissLoadingDialog();
                                    showToast("头像修改成功");
                                    file.deleteOnExit();
//                                    finish();
                                }
                            });
                    }
                }
                break;
        }
    }

    /**
     * 裁剪图片
     */
    int imageSize = 600;

    private void cropPhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", imageSize);
        intent.putExtra("outputY", imageSize);
        intent.putExtra("return-data", false);////设置为不返回数据
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        File temp = new File(headCropPath);
        temp.deleteOnExit();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp));
        intent.putExtra("scale", true);
        startActivityForResult(intent, 3);
    }


    /**
     * 权限处理
     */
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void openAlbumWithPermission() {
        headCropPath = LvAppUtils.getCommonImageSavePath(getApplicationContext()) + "/" + System.currentTimeMillis() + ".jpg";

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void openCameraWithPermission() {
        headCropPath = LvAppUtils.getCommonImageSavePath(getApplicationContext()) + "/" + System.currentTimeMillis() + ".jpg";

        File temp = new File(LvAppUtils.getCommonImageSavePath(getApplicationContext()) + "/head.jpg");
        imageUri = Uri.fromFile(temp);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 2);
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void askAgain() {
        showPermissionDialog();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showRationaleForScan(PermissionRequest request) {
        showPermissionDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UserHeaderImageActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void showPermissionDialog() {
        new AlertDialog.Builder(this)
            .setMessage("我们需要访问存储空间的权限来临时保存头像信息，否则无法更换头像")
            .setNegativeButton("取消", null)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentUtils.turnToAppDetail(UserHeaderImageActivity.this);
                }
            })
            .setCancelable(false)
            .show();
    }
}
