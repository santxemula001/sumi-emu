package com.santx.cuphead;
import org.sumi.sumi_emu.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class CupheadActivity extends AppCompatActivity {
    private static final int PICK_GAME = 41;
    private TextView selectedGame;
    private Button playButton;
    private Uri selectedUri;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(w < 0 ? w : dp(w), h < 0 ? h : dp(h));
    }

    private TextView actionButton(String label, int color) {
        TextView b = text(label, 17, Color.WHITE, true);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(12), dp(14), dp(12), dp(14));
        b.setBackground(shape(color, 14));
        b.setElevation(dp(3));
        return b;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(18, 18, 18));
        getWindow().setNavigationBarColor(Color.rgb(18, 18, 18));
        buildInterface();
    }

    private void buildInterface() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(14));
        root.setBackgroundColor(Color.rgb(18, 18, 18));

        // Left side: logo and main actions.
        LinearLayout left = column();
        left.setGravity(Gravity.CENTER);
        left.setPadding(dp(8), dp(4), dp(16), dp(4));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.cuphead_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        left.addView(logo, lp(138, 112));

        TextView title = text("CUPHEAD", 25, Color.WHITE, true);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.12f);
        LinearLayout.LayoutParams titleLp = lp(-1, -2);
        titleLp.topMargin = dp(2);
        titleLp.bottomMargin = dp(18);
        left.addView(title, titleLp);

        TextView importButton = actionButton("IMPORTAR JOGO", Color.rgb(190, 43, 43));
        left.addView(importButton, lp(-1, 58));
        importButton.setOnClickListener(v -> openGamePicker());

        playButton = (Button) null;
        TextView play = actionButton("JOGAR", Color.rgb(35, 100, 195));
        LinearLayout.LayoutParams playLp = lp(-1, 58);
        playLp.topMargin = dp(12);
        left.addView(play, playLp);
        play.setOnClickListener(v -> {
            if (selectedUri == null) {
                Toast.makeText(this, "Importe o arquivo NSP do Cuphead primeiro.", Toast.LENGTH_SHORT).show();
            } else {
                org.sumi.sumi_emu.model.Game game =
                    new org.sumi.sumi_emu.model.Game(
                        "Cuphead",
                        selectedUri.toString(),
                        "0",
                        "",
                        "",
                        false
                    );
                org.sumi.sumi_emu.activities.EmulationActivity.launch(this, game);
            }
        });

        selectedGame = text("Nenhum jogo selecionado", 12, Color.LTGRAY, false);
        selectedGame.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams selectedLp = lp(-1, -2);
        selectedLp.topMargin = dp(12);
        left.addView(selectedGame, selectedLp);

        root.addView(left, new LinearLayout.LayoutParams(0, -1, 0.43f));

        // Right side: compact settings panel.
        LinearLayout right = column();
        right.setPadding(dp(18), dp(10), dp(12), dp(10));
        right.setBackground(shape(Color.rgb(37, 37, 37), 18));

        TextView heading = text("CONFIGURAÇÕES", 17, Color.WHITE, true);
        heading.setLetterSpacing(0.08f);
        right.addView(heading, lp(-1, 34));

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        right.addView(topRow, lp(-1, 0));
        ((LinearLayout.LayoutParams) topRow.getLayoutParams()).weight = 1;

        LinearLayout graphics = settingCard("GRÁFICOS");
        TextView graphicsInfo = text("Perfil gráfico", 12, Color.LTGRAY, false);
        graphics.addView(graphicsInfo, lp(-1, 24));
        Spinner graphicsSpinner = new Spinner(this);
        String[] profiles = {"Padrão", "Desempenho", "Qualidade"};
        graphicsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, profiles));
        graphics.addView(graphicsSpinner, lp(-1, 42));
        LinearLayout.LayoutParams card1 = new LinearLayout.LayoutParams(0, -1, 1f);
        card1.rightMargin = dp(8);
        topRow.addView(graphics, card1);

        LinearLayout image = settingCard("NITIDEZ");
        TextView sharpValue = text("50%", 12, Color.LTGRAY, false);
        image.addView(sharpValue, lp(-1, 24));
        SeekBar sharpness = new SeekBar(this);
        sharpness.setMax(100);
        sharpness.setProgress(50);
        image.addView(sharpness, lp(-1, 42));
        sharpness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int value, boolean user) { sharpValue.setText(value + "%"); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        LinearLayout.LayoutParams card2 = new LinearLayout.LayoutParams(0, -1, 1f);
        topRow.addView(image, card2);

        LinearLayout bottomRow = new LinearLayout(this);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams bottomLp = new LinearLayout.LayoutParams(-1, 0, 1f);
        bottomLp.topMargin = dp(10);
        right.addView(bottomRow, bottomLp);

        LinearLayout aspect = settingCard("PROPORÇÃO DE TELA");
        Spinner aspectSpinner = new Spinner(this);
        String[] ratios = {"16:9", "16:10", "4:3", "Esticar"};
        aspectSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ratios));
        aspect.addView(aspectSpinner, lp(-1, 48));
        LinearLayout.LayoutParams card3 = new LinearLayout.LayoutParams(0, -1, 1f);
        card3.rightMargin = dp(8);
        bottomRow.addView(aspect, card3);

        LinearLayout vsync = settingCard("VSYNC");
        TextView vsyncHint = text("Sincronização vertical", 12, Color.LTGRAY, false);
        vsync.addView(vsyncHint, lp(-1, 24));
        Switch vsyncSwitch = new Switch(this);
        vsyncSwitch.setText("VSYNC");
        vsyncSwitch.setTextColor(Color.WHITE);
        vsyncSwitch.setChecked(true);
        vsync.addView(vsyncSwitch, lp(-1, 48));
        LinearLayout.LayoutParams card4 = new LinearLayout.LayoutParams(0, -1, 1f);
        bottomRow.addView(vsync, card4);

        root.addView(right, new LinearLayout.LayoutParams(0, -1, 0.57f));
        setContentView(root);
    }

    private LinearLayout settingCard(String title) {
        LinearLayout card = column();
        card.setPadding(dp(12), dp(10), dp(12), dp(8));
        card.setBackground(shape(Color.rgb(54, 54, 54), 12));
        TextView label = text(title, 13, Color.WHITE, true);
        card.addView(label, lp(-1, 26));
        return card;
    }

    private void openGamePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                "application/octet-stream",
                "application/x-nsp",
                "application/vnd.nintendo.nsp"
        });
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_GAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_GAME && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String path = uri.getLastPathSegment() == null ? "" : uri.getLastPathSegment().toLowerCase();
            if (!path.contains(".nsp")) {
                Toast.makeText(this, "Selecione um arquivo NSP.", Toast.LENGTH_LONG).show();
                return;
            }
            selectedUri = uri;
            try {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException ignored) {}
            String name = uri.getLastPathSegment();
            if (name != null && name.contains(":")) name = name.substring(name.lastIndexOf(':') + 1);
            selectedGame.setText("Jogo selecionado: " + (name == null ? "Cuphead NSP" : name));
            Toast.makeText(this, "Arquivo selecionado.", Toast.LENGTH_SHORT).show();
        }
    }
}
