package apk.zeffect.cn.vipapk;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * 应用信息管理
 *
 * @author fanjiao
 */
public class AppInfo {
    /**
     * 判断服务是否在运行。
     *
     * @param context   上下文
     * @param className 类名
     * @return 如果服务正在运行，则返回true，否则返回false。
     */
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据包名判断应用是否已经安装。
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 如果应用已经安装，则返回true，否则返回false.
     */
    public static boolean isPackageExist(Context context, String packageName) {
        boolean isExist = false;
        try {
            isExist = (null != context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES))
                    && (null != context.getPackageManager().getPackageInfo(packageName, 0));
        } catch (NameNotFoundException e) {
        } finally {
            return isExist;
        }
    }



    /**
     * （应用数值版本号比较）如果新的版本号大于当前版本号，则返回1,等于则返回0,小于则返回-1,比较出错或失败返回-2
     *
     * @param newVersionCode 新数值版本号
     * @param curVersionCode 当前的数值版本号
     * @return 如果新的版本号大于当前版本号，则返回1,等于则返回0,小于则返回-1,比较出错或失败返回-2
     */
    public static int compareVersion(int newVersionCode, int curVersionCode) {
        if (newVersionCode > curVersionCode) {
            return 1;
        } else if (newVersionCode < curVersionCode) {
            return -1;
        } else if (newVersionCode == curVersionCode) {
            return 0;
        }
        return -2;
    }

    /**
     * 比较版本号，通过切割版本号的每一个段来进行比较，支持纯数字比较和字符串比较。例如：3.2.4.3454, 3_2_4_3454；支持字符串比较，例如：v2.2.b.c,v2_2_b_c。
     *
     * @param newVersion 新的版本号
     * @param curVersion 当前版本号
     * @param separator  版本号字符串的分隔符，默认是"."
     * @return 如果新的版本号大于当前版本号，则返回1,等于则返回0,小于则返回-1,比较出错或失败返回-2。 <li>比较结果：</li> <li>1.4 > 1.3.9.9</li> <li>1.4.1 > 1.4</li> <li>a.b > a.a</li> <li>c.d < c.d.1</li>
     */
    public static int compareVersion_2(String newVersion, String curVersion, String separator) {
        int compare = -2;
        if (TextUtils.isEmpty(separator)) {
            separator = "\\.";
        }
        if (TextUtils.isEmpty(newVersion) || TextUtils.isEmpty(curVersion)) {
            return compare;
        }
        String[] newVers = newVersion.split(separator);
        String[] curVers = curVersion.split(separator);
        if (newVers == null || curVers == null) {
            return compare;
        }
        int len = newVers.length <= curVers.length ? newVers.length : curVers.length;
        for (int i = 0; i < len; i++) {
            String Sver_new = newVers[i];
            String Sver_cur = curVers[i];
            try {
                int ver_new = Integer.valueOf(Sver_new);
                int ver_cur = Integer.valueOf(Sver_cur);
                if (ver_new > ver_cur) {
                    compare = 1;
                    break;
                } else if (ver_new < ver_cur) {
                    compare = -1;
                    break;
                } else if (ver_new == ver_cur) {
                    // 版本号长度不一致时，更长的版本号更高。
                    if (i == len - 1) {
                        if (newVers.length > curVers.length) {
                            compare = 1;
                        } else if (newVers.length < curVers.length) {
                            compare = -1;
                        } else {
                            compare = 0;
                        }
                    } else {
                        compare = 0;
                    }
                }
            } catch (Exception e) {
                compare = Sver_new.compareToIgnoreCase(Sver_cur);
                if (compare != 0) {
                    break;
                }
                if (i == len - 1) {
                    if (newVers.length > curVers.length) {
                        compare = 1;
                    } else if (newVers.length < curVers.length) {
                        compare = -1;
                    } else {
                        compare = 0;
                    }
                }
            }
        }
        return compare;
    }

    /**
     * 根据包名来获取当前安装的应用程序版本号，并且和新的版本号比较，可用于判断当前应用是否需要升级。
     *
     * @param context     上下文
     * @param packageName 包名
     * @param newVersion  新的版本号
     * @param separator   版本号的分隔符
     * @return 如果应用程序不存在或者版本号较低，返回true，否则返回false。
     */
    public static boolean isPackageOlder(Context context, String packageName, String newVersion, String separator) {

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

        for (PackageInfo info : infoList) {
            if (packageName.equals(info.packageName)) {
                String curVersion = info.versionName;
                int compare = compareVersion_2(newVersion, curVersion, separator);
                return compare == 1;
//                if (compare == 1) {
//                    return true;
//                } else {
//                    return false;
//                }
            }
        }
        return true;
    }

    /**
     * 根据包名来获取当前安装的应用程序版本号，并且和新的版本号比较，可用于判断当前应用是否需要升级。
     *
     * @param context
     *            上下文
     * @param packageName
     *            包名
     * @return 如果应用程序不存在或者版本号较低，返回true，否则返回false。
     */
    // public static boolean IsPackageOld(Context context, String packageName,
    // String newVersion) {
    //
    // PackageManager pm = context.getPackageManager();
    // List<PackageInfo> infoList =
    // pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
    //
    // for (PackageInfo info : infoList) {
    // if (packageName.equals(info.packageName)) {
    // String curVersion = info.versionName;
    // if (curVersion.compareToIgnoreCase(newVersion) < 0) {
    // return true;
    // } else {
    // return false;
    // }
    // }
    // }
    // return true;
    // }



    /**
     * 获取手机上的非系统应用列表。
     *
     * @param context 上下文
     * @return 应用信息列表
     */
    public static List<PackageInfo> getUserInstallAppInfo(Context context) {
        List<PackageInfo> pinfos = getAllInstalledAppInfo(context);
        List<PackageInfo> UserApp = new ArrayList<>();
        if (pinfos == null || pinfos.size() <= 0) {
            return null;
        }
        // 遍历每个应用包信息
        for (PackageInfo info : pinfos) {
            ApplicationInfo appInfo = info.applicationInfo;
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // 非系统应用。
                UserApp.add(info);
            }
        }
        return UserApp;
    }

    /**
     * 获取手机上的系统应用列表。
     *
     * @param context 上下文
     * @return 应用信息列表
     */
    public static List<PackageInfo> getSystemInstallAppInfo(Context context) {
        List<PackageInfo> pinfos = getAllInstalledAppInfo(context);
        List<PackageInfo> UserApp = new ArrayList<>();
        if (pinfos == null || pinfos.size() <= 0) {
            return null;
        }
        // 遍历每个应用包信息
        for (PackageInfo info : pinfos) {
            ApplicationInfo appInfo = info.applicationInfo;
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                // 系统应用。
                UserApp.add(info);
            }
        }
        return UserApp;
    }





    /**
     * 获取手机上所有的应用列表。
     *
     * @param context 上下文
     * @return 应用信息列表
     */
    public static List<PackageInfo> getAllInstalledAppInfo(Context context) {
        // 获取所有的安装在手机上的应用软件的信息，并且获取这些软件里面的权限信息
        PackageManager pm = context.getPackageManager();// 获取系统应用包管理
        // 获取每个包内的androidmanifest.xml信息，它的权限等等
        List<PackageInfo> pinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        return pinfos;
    }

    /**
     * 启动另外一个应用
     *
     * @param context     上下文
     * @param packageName 待启动的应用的包名
     */
    public static void RunOtherApp(Context context, String packageName) {
        RunOtherApp(context, packageName, new Bundle(), null);
    }

    /**
     * 启动另外一个应用
     *
     * @param context     上下文
     * @param uri         应用特定的uri，用于传递数据。比如：应用的AndroidManifest.xml中，启动界面设置data属性（ &lt;data android:scheme="qimon"&gt;&lt;/data&gt; ），则在启动该界面的时候，必须给Intent设置一个uri参数，该参数格式为：Uri.paser("qimon://xxx");
     *                    默认情况下，传入null。
     * @param extras      附带的信息（可为null）
     * @param packageName 待启动的应用的包名
     */
    public static void RunOtherApp(Context context, String packageName, Bundle extras, Uri uri) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, uri);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = null;
            try {
                if (apps != null) {
                    ri = (ResolveInfo) apps.iterator().next();
                }
            } catch (Exception e) {
//                Toast.makeText(context, "该应用没有启动界面，无法使用。", Toast.LENGTH_SHORT).show();
                // 使用与PackageName相同的Action， 打开应用
                RunOtherAppSpecialAction(context, packageName, packageName, extras, uri);
                return;
            }
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                RunOtherApp(context, packageName, className, extras);
            } else {
                Toast.makeText(context, "手机尚未安装该应用，请下载并安装后再使用。", Toast.LENGTH_SHORT).show();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "手机尚未安装该应用，请下载并安装后再使用。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 启动另外一个应用（Action与包名一样）
     *
     * @param context     上下文
     * @param packageName 待启动的应用的包名
     * @param action      Intent的Action。用于打开不显示在默认界面的应用。
     */
    public static void RunOtherApp(Context context, String packageName, String action) {
        RunOtherApp(context, packageName, action, null);
    }

    /**
     * 启动另外一个应用（Action与包名一样）
     *
     * @param context     上下文
     * @param packageName 待启动的应用的包名
     * @param uri         应用特定的uri，用于传递数据。比如：应用的AndroidManifest.xml中，启动界面设置data属性（ &lt;data android:scheme="qimon"&gt;&lt;/data&gt; ），则在启动该界面的时候，必须给Intent设置一个uri参数，该参数格式为：Uri.paser("qimon://xxx");
     *                    默认情况下，传入null。
     * @param extras      附带的信息（可为null）
     * @param action      Intent的Action。用于打开不显示在默认界面的应用。
     */
    public static void RunOtherAppSpecialAction(Context context, String packageName, String action, Bundle extras, Uri uri) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(action, uri);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = null;
            try {
                if (apps != null) {
                    ri = (ResolveInfo) apps.iterator().next();
                }
            } catch (Exception e) {
                Toast.makeText(context, "该应用没有启动界面，无法使用。", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                RunOtherApp(context, packageName, className, extras);
            } else {
                Toast.makeText(context, "手机尚未安装该应用，请下载并安装后再使用。", Toast.LENGTH_SHORT).show();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "手机尚未安装该应用，请下载并安装后再使用。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开其他应用的主界面
     *
     * @param context              上下文
     * @param packageName          其他应用包名
     * @param activityAbsolutePath 指定的activity全路径
     * @param extras               附带的信息（可为null）
     */
    public static void RunOtherApp(Context context, String packageName, String activityAbsolutePath, Bundle extras) {
        if (context == null || TextUtils.isEmpty(packageName) || TextUtils.isEmpty(activityAbsolutePath)) {
            Toast.makeText(context, "该应用没有启动界面，无法使用。", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent intent = new Intent();
            ComponentName cn = new ComponentName(packageName, activityAbsolutePath);
            intent.setComponent(cn);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (extras != null) {
                intent.putExtras(extras);
            }
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, 100);
            } else {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "手机尚未安装该应用，请下载并安装后再使用。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开其他应用的指定activity界面
     * <ul>
     * 注意，打开的activity必须要在xml中增加以下配置信息，否则无法在其他应用中被打开：
     * <li>&lt;intent-filter&gt;</li>
     * <li>&lt;action android:name="android.intent.action.VIEW" /&gt;</li>
     * <li>&lt;category android:name="android.intent.category.DEFAULT" /&gt;</li>
     * <li>&lt;/intent-filter&gt;</li>
     * </ul>
     *
     * @param context              上下文
     * @param packageName          其他应用包名
     * @param activityAbsolutePath 指定的activity全路径
     */
    public static void OpenOtherAppTargetActivity(Context context, String packageName, String activityAbsolutePath) {
        OpenOtherAppTargetActivity(context, packageName, activityAbsolutePath, null, null);
    }

    /**
     * 打开其他应用的指定activity界面
     * <ul>
     * 注意，打开的activity必须要在xml中增加以下配置信息，否则无法在其他应用中被打开：
     * <li>&lt;intent-filter&gt;</li>
     * <li>&lt;action android:name="android.intent.action.VIEW" /&gt;</li>
     * <li>&lt;category android:name="android.intent.category.DEFAULT" /&gt;</li>
     * <li>&lt;/intent-filter&gt;</li>
     * </ul>
     *
     * @param context              上下文
     * @param packageName          其他应用包名
     * @param activityAbsolutePath 指定的activity全路径
     * @param extras               附带的信息（可为null）
     */
    public static void OpenOtherAppTargetActivity(Context context, String packageName, String activityAbsolutePath, Bundle extras) {
        OpenOtherAppTargetActivity(context, packageName, activityAbsolutePath, extras, null);
    }

    /**
     * 打开其他应用的指定activity界面
     * <ul>
     * 注意，打开的activity必须要在xml中增加以下配置信息，否则无法在其他应用中被打开：
     * <li>&lt;intent-filter&gt;</li>
     * <li>&lt;action android:name="android.intent.action.VIEW" /&gt;</li>
     * <li>&lt;category android:name="android.intent.category.DEFAULT" /&gt;</li>
     * <li>&lt;/intent-filter&gt;</li>
     * </ul>
     *
     * @param context              上下文
     * @param packageName          其他应用包名
     * @param activityAbsolutePath 指定的activity全路径
     * @param extras               附带的信息（可为null）
     * @param uri                  用于传递制定格式的数据，比如：应用的AndroidManifest.xml中，启动界面设置data属性（ &lt;data android:scheme="qimon"&gt;&lt;/data&gt; ），则在启动该界面的时候，必须给Intent设置一个uri参数，该参数格式为：Uri.paser("qimon://xxx");
     *                             默认情况下，传入null。
     */
    public static void OpenOtherAppTargetActivity(Context context, String packageName, String activityAbsolutePath, Bundle extras, Uri uri) {
        OpenOtherAppTargetActivity(context, packageName, activityAbsolutePath, extras, null, 100);
    }

    /**
     * 打开其他应用的指定activity界面
     * <ul>
     * 注意，打开的activity必须要在xml中增加以下配置信息，否则无法在其他应用中被打开：
     * <li>&lt;intent-filter&gt;</li>
     * <li>&lt;action android:name="android.intent.action.VIEW" /&gt;</li>
     * <li>&lt;category android:name="android.intent.category.DEFAULT" /&gt;</li>
     * <li>&lt;/intent-filter&gt;</li>
     * </ul>
     *
     * @param context              上下文
     * @param packageName          其他应用包名
     * @param activityAbsolutePath 指定的activity全路径
     * @param extras               附带的信息（可为null）
     * @param uri                  用于传递制定格式的数据，比如：应用的AndroidManifest.xml中，启动界面设置data属性（ &lt;data android:scheme="qimon"&gt;&lt;/data&gt; ），则在启动该界面的时候，必须给Intent设置一个uri参数，该参数格式为：Uri.paser("qimon://xxx");
     *                             默认情况下，传入null。
     */
    public static void OpenOtherAppTargetActivity(Context context, String packageName, String activityAbsolutePath, Bundle extras, Uri uri, int requestCode) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, uri);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent, 0);

            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                Intent intent = new Intent();
                ComponentName cn = new ComponentName(packageName, activityAbsolutePath);
                intent.setComponent(cn);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (extras != null) {
                    intent.putExtras(extras);
                }
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, requestCode);
                } else {
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "手机尚未安装该应用，请下载并安装后再使用。", Toast.LENGTH_SHORT).show();
        }
    }
}
