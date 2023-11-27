package org.com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        TextView logTextView = findViewById(R.id.logTextView); // Make sure this ID matches your TextView in activity_log.xml
        StringBuilder formattedText = new StringBuilder();

        for (String logEntry : MainActivity.playLog) {
            formattedText.append(Html.fromHtml(logEntry)).append("\n\n");
        }

        logTextView.setText(formattedText.toString());

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }
}