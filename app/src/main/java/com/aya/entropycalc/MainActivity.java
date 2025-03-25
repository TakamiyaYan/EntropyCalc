package com.aya.entropycalc;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import org.apache.commons.math3.fraction.Fraction;

public class MainActivity extends AppCompatActivity {

    private EditText inputValues;
    private RadioGroup radioGroup;
    private TextView resultText, resultSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputValues = findViewById(R.id.inputValues);
        radioGroup = findViewById(R.id.radioGroup);
        resultText = findViewById(R.id.resultText);
        resultSteps = findViewById(R.id.resultSteps);
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(v -> calculateEntropy());
    }

    // 加载菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // 处理菜单点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            showToast("信息熵计算器 v0.1.0(Beta)\n作者: 多分猫猫");
            return true;
        } else if (id == R.id.action_help) {
            showToast("请输入概率值，并选择对数底计算信息熵。\n概率用逗号分隔，可使用分数格式。例如：1/2,1/2");
            return true;
        } else if (id == R.id.action_formula) {
            showToast("信息熵公式:\nH(X) = -Σ p(x) log_b p(x)");
        }
        return super.onOptionsItemSelected(item);
    }

    private void calculateEntropy() {
        String input = inputValues.getText().toString();
        String[] tokens = input.split(",");
        double[] probabilities = new double[tokens.length];

        try {
            double sum = 0.0;
            for (int i = 0; i < tokens.length; i++) {
                probabilities[i] = parseFraction(tokens[i].trim());
                sum += probabilities[i];
            }

            double base = 2;
            if (radioGroup.getCheckedRadioButtonId() == R.id.logBaseE) {
                base = Math.E;
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.logBase10) {
                base = 10;
            }

            // 计算信息熵
            StringBuilder steps = new StringBuilder("H(X) = -(");
            double entropy = 0;
            for (double p : probabilities) {
                if (p > 0) {
                    double logValue = Math.log(p) / Math.log(base);
                    double term = -p * logValue;
                    steps.append(formatNumber(p))
                            .append(" × log<sub>").append(formatNumber(base))
                            .append("</sub>(").append(formatNumber(p)).append(") + ");
                    entropy += term;
                }
            }
            steps.setLength(steps.length() - 3); // 移除最后的“+ ”
            steps.append(")\n= ");

            for (double p : probabilities) {
                if (p > 0) {
                    double logValue = Math.log(p) / Math.log(base);
                    double term = -p * logValue;
                    steps.append(formatNumber(term)).append(" + ");
                }
            }
            steps.setLength(steps.length() - 3);
            steps.append("\n= ").append(formatNumber(entropy));

            // 计算概率和误差
            double epsilon = 0.0001;
            if (Math.abs(sum - 1.0) > epsilon) {
                steps.append("\n⚠️ 注意: 概率和为 ").append(formatNumber(sum))
                        .append("，不等于 1，结果可能无效");
            }

            resultSteps.setText(Html.fromHtml(steps.toString(), Html.FROM_HTML_MODE_COMPACT));
            resultText.setText(getString(R.string.entropy_result, formatNumber(entropy)));
        } catch (Exception e) {
            resultText.setText("输入错误，请输入有效概率值！");
            resultSteps.setText("");
        }
    }

    private double parseFraction(String input) {
        if (Pattern.matches("\\d+/\\d+", input)) {
            String[] parts = input.split("/");
            return new Fraction(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])).doubleValue();
        }
        return Double.parseDouble(input);
    }

    private String formatNumber(double value) {
        DecimalFormat df = new DecimalFormat("#.####");
        return df.format(value);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}


