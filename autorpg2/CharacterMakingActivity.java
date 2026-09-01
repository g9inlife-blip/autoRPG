package com.shirobakama.autorpg2;

import android.R;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.view.HelpDialogFragment;
import com.shirobakama.logquest2.C0380R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class CharacterMakingActivity extends FragmentActivity implements View.OnClickListener {
    private static final int BONUS_NORMAL = 8;
    private static final String RANDOM_NAME_RESOURCE_PREFIX = "res_charname_random";
    private static final int REQUEST_CODE_CROP_IMAGE = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final String STATE_BONUS_POINT = "bonus_point";
    private static final String STATE_CHARACTER_BITMAP = "character_bitmap";
    private static final String STATE_PLAYER_CHAR = "player_char";
    protected static final String TAG = "char-making";
    private static final String TEMP_FILE_NAME_CROPPED = "shirobakama_autorpg2_temp_image_cropped.png";
    private static List<String> randomNames;
    private int mBonusPoint;
    private Bitmap mCharacterBitmap;
    private EditText mEtName;
    private GameContext mGame;
    private ImageView mIvCharacter;
    private boolean mMovingToAnotherActivity;
    private PlayerChar mPlayerChar;
    private Random mRandom;
    private Spinner mSpnClass;
    private TextView mTvBonusPoint;
    private static final int[] ID_BTN_UP = {C0380R.id.btnStrUp, C0380R.id.btnIntUp, C0380R.id.btnAgiUp, C0380R.id.btnVitUp};
    private static final int[] ID_BTN_DOWN = {C0380R.id.btnStrDown, C0380R.id.btnIntDown, C0380R.id.btnAgiDown, C0380R.id.btnVitDown};
    private static final int[] ID_TV = {C0380R.id.tvStr, C0380R.id.tvInt, C0380R.id.tvAgi, C0380R.id.tvVit};
    private static final int MAX_PRESET_BITMAPS = PlayerChar.BITMAP_RESOURCE_IDS.length;
    private TextView[] mTvStatus = new TextView[GameChar.NUMBER_OF_ATTRIBUTE];
    private Button[] mBtnStatusUp = new Button[GameChar.NUMBER_OF_ATTRIBUTE];
    private Button[] mBtnStatusDown = new Button[GameChar.NUMBER_OF_ATTRIBUTE];

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DeviceUtil.setLocaleIfNeeded(this, null);
        setContentView(C0380R.layout.character_making);
        DeviceUtil.handleOrientation(this, null);
        TableLayout tableLayout = (TableLayout) findViewById(C0380R.id.tlayArea1);
        TableLayout tableLayout2 = (TableLayout) findViewById(C0380R.id.tlayArea2);
        tableLayout.setColumnStretchable(1, true);
        tableLayout2.setColumnStretchable(1, true);
        this.mEtName = (EditText) findViewById(C0380R.id.etName);
        for (int i = 0; i < GameChar.NUMBER_OF_ATTRIBUTE; i++) {
            this.mBtnStatusUp[i] = (Button) findViewById(ID_BTN_UP[i]);
            this.mBtnStatusDown[i] = (Button) findViewById(ID_BTN_DOWN[i]);
            this.mTvStatus[i] = (TextView) findViewById(ID_TV[i]);
            this.mBtnStatusUp[i].setOnClickListener(this);
            this.mBtnStatusDown[i].setOnClickListener(this);
        }
        findViewById(C0380R.id.btnOK).setOnClickListener(this);
        findViewById(C0380R.id.btnNameRandom).setOnClickListener(this);
        findViewById(C0380R.id.btnImagePick).setOnClickListener(this);
        findViewById(C0380R.id.btnImageRandom).setOnClickListener(this);
        findViewById(C0380R.id.btnHelp).setOnClickListener(this);
        findViewById(C0380R.id.btnCancel).setOnClickListener(this);
        Spinner spinner = (Spinner) findViewById(C0380R.id.spnRace);
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource = ArrayAdapter.createFromResource(this, C0380R.array.race, R.layout.simple_spinner_item);
        arrayAdapterCreateFromResource.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.shirobakama.autorpg2.CharacterMakingActivity.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j) {
                CharacterMakingActivity.this.resetRace(PlayerChar.Race.values()[i2]);
                CharacterMakingActivity.this.refresh();
            }
        });
        this.mSpnClass = (Spinner) findViewById(C0380R.id.spnClass);
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource2 = ArrayAdapter.createFromResource(this, C0380R.array.clazz, R.layout.simple_spinner_item);
        arrayAdapterCreateFromResource2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mSpnClass.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource2);
        this.mTvBonusPoint = (TextView) findViewById(C0380R.id.tvBonusPoint);
        this.mIvCharacter = (ImageView) findViewById(C0380R.id.ivCharacter);
        this.mIvCharacter.setOnClickListener(this);
        this.mIvCharacter.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.shirobakama.autorpg2.CharacterMakingActivity.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                return CharacterMakingActivity.this.imageOnLongClick(view);
            }
        });
        prepareRandomNames();
        this.mRandom = new Random();
        if (bundle != null) {
            this.mGame = (GameContext) bundle.getParcelable("game");
            this.mPlayerChar = (PlayerChar) bundle.getParcelable("player_char");
            this.mBonusPoint = bundle.getInt(STATE_BONUS_POINT);
            this.mCharacterBitmap = (Bitmap) bundle.getParcelable(STATE_CHARACTER_BITMAP);
            this.mGame.calcCharacterStatus(this);
            this.mPlayerChar.calcStatus((Context) this, this.mGame);
        } else {
            this.mGame = GameContext.game;
            GameContext.game = null;
            this.mPlayerChar = new PlayerChar();
            PlayerChar playerChar = this.mPlayerChar;
            playerChar.race = null;
            playerChar.presetBitmapId = pickRandomImageId(-1);
            resetRace(PlayerChar.Race.HUMAN);
            this.mSpnClass.setSelection(0);
            this.mPlayerChar.clazz = GameChar.CharClass.values()[0];
        }
        refresh();
        refreshImage();
        setResult(0);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        File croppedTempFile = getCroppedTempFile();
        if (croppedTempFile != null) {
            croppedTempFile.delete();
        }
    }

    private void prepareRandomNames() {
        randomNames = new ArrayList();
        int i = 1;
        while (true) {
            int identifier = getResources().getIdentifier(RANDOM_NAME_RESOURCE_PREFIX + i, "string", getPackageName());
            if (identifier == 0) {
                return;
            }
            randomNames.add(getString(identifier));
            i++;
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mMovingToAnotherActivity = false;
        if (this.mGame.characters.size() > 3) {
            finish();
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("game", this.mGame);
        bundle.putParcelable("player_char", this.mPlayerChar);
        bundle.putInt(STATE_BONUS_POINT, this.mBonusPoint);
        bundle.putParcelable(STATE_CHARACTER_BITMAP, this.mCharacterBitmap);
    }

    boolean canMoveToAnotherActivity() {
        if (this.mMovingToAnotherActivity) {
            return false;
        }
        this.mMovingToAnotherActivity = true;
        return true;
    }

    protected void finishMaking() throws IOException {
        this.mPlayerChar.clazz = GameChar.CharClass.values()[this.mSpnClass.getSelectedItemPosition()];
        PlayerChar playerChar = this.mPlayerChar;
        playerChar.exp = 0;
        playerChar.level = 1;
        playerChar.name = this.mEtName.getText().toString();
        if (this.mPlayerChar.name == null || this.mPlayerChar.name.length() == 0) {
            this.mPlayerChar.name = pickRandomName();
        }
        this.mPlayerChar.calcStatus((Context) this, this.mGame);
        this.mPlayerChar.restoreHpMp();
        Persister persister = new Persister(this);
        try {
            persister.writePlayer(this.mPlayerChar);
            if (this.mCharacterBitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                if (this.mCharacterBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)) {
                    persister.writeCharacterBitmap(this.mPlayerChar.f106id, byteArrayOutputStream.toByteArray());
                }
                try {
                    byteArrayOutputStream.close();
                } catch (IOException unused) {
                }
            }
            GameContext.game = this.mGame;
            setResult(-1);
            finish();
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
    }

    protected void resetRace(PlayerChar.Race race) {
        if (this.mPlayerChar.race == race) {
            return;
        }
        PlayerChar playerChar = this.mPlayerChar;
        playerChar.race = race;
        playerChar.setDefaultStatus();
        this.mBonusPoint = 8;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        for (int i = 0; i < this.mBtnStatusUp.length; i++) {
            if (view.getId() == this.mBtnStatusUp[i].getId()) {
                btnAttrUpOnClick(i);
                return;
            } else {
                if (view.getId() == this.mBtnStatusDown[i].getId()) {
                    btnAttrDownOnClick(i);
                    return;
                }
            }
        }
        if (view.getId() == 2131165270) {
            btnNameRandomOnClick();
            return;
        }
        if (view.getId() == 2131165260) {
            btnImageRandomOnClick();
            return;
        }
        if (view.getId() == 2131165259) {
            btnImagePickOnClick();
            return;
        }
        if (view.getId() == 2131165271) {
            btnOkOnClick();
            return;
        }
        if (view.getId() == 2131165244) {
            btnCancelOnClick();
        } else if (view.getId() == 2131165258) {
            btnHelpOnClick();
        } else if (view.getId() == 2131165332) {
            imageOnClick();
        }
    }

    private void btnNameRandomOnClick() {
        this.mEtName.setText(pickRandomName());
    }

    private String pickRandomName() {
        List<String> list = randomNames;
        return list.get(this.mRandom.nextInt(list.size()));
    }

    private void btnImageRandomOnClick() {
        PlayerChar playerChar = this.mPlayerChar;
        playerChar.presetBitmapId = pickRandomImageId(playerChar.presetBitmapId);
        this.mCharacterBitmap = null;
        refreshImage();
    }

    private void imageOnClick() {
        PlayerChar playerChar = this.mPlayerChar;
        playerChar.presetBitmapId = (playerChar.presetBitmapId + 1) % MAX_PRESET_BITMAPS;
        this.mCharacterBitmap = null;
        refreshImage();
    }

    protected boolean imageOnLongClick(View view) {
        this.mPlayerChar.presetBitmapId--;
        if (this.mPlayerChar.presetBitmapId < 0) {
            this.mPlayerChar.presetBitmapId = MAX_PRESET_BITMAPS - 1;
        }
        this.mCharacterBitmap = null;
        refreshImage();
        return true;
    }

    private int pickRandomImageId(int i) {
        int iNextInt = i;
        while (iNextInt == i) {
            iNextInt = this.mRandom.nextInt(MAX_PRESET_BITMAPS);
        }
        return iNextInt;
    }

    private void btnImagePickOnClick() {
        if (canMoveToAnotherActivity()) {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }
    }

    private void btnHelpOnClick() {
        HelpDialogFragment.show(this, getString(C0380R.string.msg_help_character_making) + getString(C0380R.string.msg_help_subclass_status));
    }

    private File getCroppedTempFile() {
        if (isSdcardMounted()) {
            return new File(Environment.getExternalStorageDirectory(), TEMP_FILE_NAME_CROPPED);
        }
        return null;
    }

    private boolean isSdcardMounted() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    private void btnOkOnClick() {
        if (this.mBonusPoint > 0) {
            Toast.makeText(this, C0380R.string.msg_use_all_bonus, 0).show();
            return;
        }
        if (TextUtils.isEmpty(this.mEtName.getText().toString())) {
            Toast.makeText(this, C0380R.string.msg_name_is_selected_randomly, 0).show();
        }
        AlertDialogFragment.Decorator.confirmationDecorator(C0380R.string.msg_dlg_title_char_ok, C0380R.string.msg_dlg_is_this_ok).decorate(new MakingConfirmDialogFragment()).show(getSupportFragmentManager());
    }

    private void btnCancelOnClick() {
        finish();
    }

    public static class MakingConfirmDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) throws IOException {
            if (i != -1) {
                return;
            }
            ((CharacterMakingActivity) getActivity()).finishMaking();
        }
    }

    private void btnAttrUpOnClick(int i) {
        GameChar.Attribute attribute;
        int baseAttr;
        if (this.mBonusPoint > 0 && (baseAttr = this.mPlayerChar.getBaseAttr((attribute = GameChar.ATTRIBUTES[i]))) < 18) {
            this.mPlayerChar.setBaseAttr(attribute, baseAttr + 1);
            this.mBonusPoint--;
            refresh();
        }
    }

    private void btnAttrDownOnClick(int i) {
        GameChar.Attribute attribute = GameChar.ATTRIBUTES[i];
        int baseAttr = this.mPlayerChar.getBaseAttr(attribute);
        if (baseAttr <= PlayerChar.getMinimumAttr(this.mPlayerChar.race, attribute)) {
            return;
        }
        this.mPlayerChar.setBaseAttr(attribute, baseAttr - 1);
        this.mBonusPoint++;
        refresh();
    }

    protected void refresh() {
        GameChar.Attribute[] attributeArr = GameChar.ATTRIBUTES;
        int i = 0;
        while (true) {
            TextView[] textViewArr = this.mTvStatus;
            if (i < textViewArr.length) {
                textViewArr[i].setText(Integer.toString(this.mPlayerChar.getBaseAttr(attributeArr[i])));
                i++;
            } else {
                this.mTvBonusPoint.setText(Integer.toString(this.mBonusPoint));
                return;
            }
        }
    }

    private void refreshImage() {
        if (this.mPlayerChar.presetBitmapId >= 0) {
            this.mIvCharacter.setImageDrawable(getResources().getDrawable(PlayerChar.BITMAP_RESOURCE_IDS[this.mPlayerChar.presetBitmapId]));
            return;
        }
        Bitmap bitmap = this.mCharacterBitmap;
        if (bitmap != null) {
            this.mIvCharacter.setImageBitmap(bitmap);
        } else {
            this.mIvCharacter.setImageBitmap(null);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) throws IOException {
        Bitmap bitmapProcessImagePicked;
        File croppedTempFile;
        boolean z = false;
        if (i != 1) {
            if (i == 2 && i2 == -1) {
                if (intent == null || (croppedTempFile = getCroppedTempFile()) == null) {
                    bitmapProcessImagePicked = null;
                } else {
                    bitmapProcessImagePicked = processImagePicked(croppedTempFile);
                    if (bitmapProcessImagePicked != null) {
                        z = true;
                    }
                }
                if (z) {
                    this.mCharacterBitmap = bitmapProcessImagePicked;
                    this.mPlayerChar.presetBitmapId = -1;
                } else {
                    if (this.mPlayerChar.presetBitmapId < 0) {
                        this.mPlayerChar.presetBitmapId = pickRandomImageId(-1);
                    }
                    this.mCharacterBitmap = null;
                    Toast.makeText(this, C0380R.string.msg_no_image_returned, 1).show();
                }
                refreshImage();
                return;
            }
            return;
        }
        if (i2 == -1) {
            Uri data = intent.getData();
            if (data == null) {
                Toast.makeText(this, C0380R.string.msg_no_image_returned, 1).show();
                return;
            }
            String[] strArr = {"com.android.gallery", "com.cooliris.media", "com.google.android.gallery3d"};
            String[] strArr2 = {"com.android.camera.CropImage", "com.cooliris.media.CropImage", "com.android.gallery3d.app.CropImage"};
            int i3 = 0;
            while (true) {
                if (i3 >= strArr.length) {
                    i3 = -1;
                    break;
                }
                Intent intent2 = new Intent();
                intent2.setClassName(strArr[i3], strArr2[i3]);
                intent2.setType("image/*");
                if (!getPackageManager().queryIntentActivities(intent2, 0).isEmpty()) {
                    break;
                } else {
                    i3++;
                }
            }
            File croppedTempFile2 = getCroppedTempFile();
            if (croppedTempFile2 == null) {
                Toast.makeText(this, C0380R.string.msg_file_output_failed, 1).show();
                return;
            }
            try {
                croppedTempFile2.createNewFile();
                int desiredBitmapWidth = PlayerChar.getDesiredBitmapWidth(getResources().getDisplayMetrics().densityDpi);
                try {
                    Intent intent3 = new Intent();
                    if (i3 >= 0) {
                        intent3.setClassName(strArr[i3], strArr2[i3]);
                    } else {
                        intent3.setAction("com.android.camera.action.CROP");
                    }
                    intent3.setDataAndType(data, "image/*");
                    intent3.putExtra("crop", "true");
                    intent3.putExtra("aspectX", 1);
                    intent3.putExtra("aspectY", 1);
                    intent3.putExtra("outputX", desiredBitmapWidth);
                    intent3.putExtra("outputY", desiredBitmapWidth);
                    intent3.putExtra("scale", true);
                    intent3.putExtra("return-data", false);
                    intent3.putExtra("output", Uri.fromFile(croppedTempFile2));
                    intent3.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                    intent3.putExtra("noFaceDetection", true);
                    startActivityForResult(intent3, 2);
                } catch (ActivityNotFoundException unused) {
                    Toast.makeText(this, C0380R.string.msg_no_image_picker, 1).show();
                }
            } catch (IOException unused2) {
                Toast.makeText(this, C0380R.string.msg_file_output_failed, 1).show();
            }
        }
    }

    private Bitmap processImagePicked(File file) {
        Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bitmapDecodeFile == null) {
            return null;
        }
        int desiredBitmapWidth = PlayerChar.getDesiredBitmapWidth(getResources().getDisplayMetrics().densityDpi);
        if (bitmapDecodeFile.getWidth() != desiredBitmapWidth) {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(desiredBitmapWidth, desiredBitmapWidth, Bitmap.Config.ARGB_8888);
            Matrix matrix = new Matrix();
            float width = desiredBitmapWidth / bitmapDecodeFile.getWidth();
            matrix.postScale(width, width);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            canvas.drawARGB(0, 255, 255, 255);
            canvas.drawBitmap(bitmapDecodeFile, matrix, null);
            bitmapDecodeFile.recycle();
            bitmapDecodeFile = bitmapCreateBitmap;
        }
        bitmapDecodeFile.setDensity(PlayerChar.getCharBitmapDensity(bitmapDecodeFile.getWidth()));
        return bitmapDecodeFile;
    }
}
