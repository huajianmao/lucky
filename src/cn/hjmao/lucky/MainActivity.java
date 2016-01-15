package cn.hjmao.lucky;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends Activity {
  private Button switchPlugin;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    switchPlugin = (Button) findViewById(R.id.button_accessible);

    handleMiuiStatusBar();
    updateServiceStatus();
  }

  @Override
  protected void onResume() {
    super.onResume();
    updateServiceStatus();
  }

  @Override
  protected void onDestroy() {
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    super.onDestroy();
  }

  private void handleMiuiStatusBar() {
    Window window = getWindow();

    Class clazz = window.getClass();
    try {
      int tranceFlag = 0;
      Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

      Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
      tranceFlag = field.getInt(layoutParams);

      Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
      extraFlagField.invoke(window, tranceFlag, tranceFlag);
    } catch (Exception e) {
    }
  }

  private void updateServiceStatus() {
    boolean serviceEnabled = false;
    AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
    List<AccessibilityServiceInfo> services = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
    for (AccessibilityServiceInfo service : services) {
      if (service.getId().equals(getPackageName() + "/.HongbaoService")) {
        serviceEnabled = true;
        break;
      }
    }

    if (serviceEnabled) {
      switchPlugin.setText("关闭插件");
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      switchPlugin.setText("开启插件");
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  }

  private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

  public void onButtonClicked(View view) {
    startActivity(mAccessibleIntent);
  }

  public void openGithub(View view) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/huajianmao/lucky"));
    startActivity(browserIntent);
  }
}