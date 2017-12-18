package apk.zeffect.cn.vipapk

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
//跳过收费显示界面  http://vipapp.6655.la/1/1.html
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gotoVipYouku.setOnClickListener {
            AppInfo.RunOtherApp(this
                    , "com.youku.phone"
                    , "com.youku.ui.activity.HomePageActivity"
                    , null)
        }
    }


}
