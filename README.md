# FloatWindow
悬浮运营框

kotlin语言编写

```kotlin
class MainActivity : AppCompatActivity() {
    lateinit var floatWindow: FloatWindow
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view: View = LayoutInflater.from(this).inflate(R.layout.float_layout, null)
        val imageView:ImageView = view.findViewById(R.id.imageView)
        floatWindow = FloatWindow(this, view).apply {
            mView = view
            //设置宽度
            setWidth(Screen.WIDTH, 0.2f)
            //设置高度
            setHeight(Screen.WIDTH, 0.2f)
            //设置所在屏幕的位置，宽度的80%
            setX(Screen.WIDTH, 0.8f)
            //设置所在屏幕的位置，高度的70%
            setY(Screen.HEIGHT, 0.7f)
            //设置拖动类型
            setMoveType(MoveType.ACTIVE, 100, 0)
            setMoveStyle(500, BounceInterpolator())
            //悬浮视图的监听器
            mViewStateListener = mStateListener
            //如果需要设置全局悬浮，则需要悬浮窗权限
            mPermissionListener = mPerListener
            //拖动视图是哪个，这样可以将复杂视图分离，如布局中有个关闭按钮，有个拖动的，拖动的视图还可以点击
            mDragView = imageView
            //显示
            show()
        }


        imageView.setOnClickListener {
            Toast.makeText(this,"点击事件",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        floatWindow.dismiss()
    }

    override fun onStart() {
        super.onStart()
        floatWindow.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatWindow.clear()
    }

    private val mPerListener: PermissionListener = object : PermissionListener {
        override fun onSuccess() {
            Log.d("MainActivity:", "onSuccess")
        }

        override fun onFail() {
            Log.d("MainActivity:", "onFail")
        }
    }

    private val mStateListener: ViewStateListener = object : ViewStateListener {
        override fun onPositionUpdate(x: Int, y: Int) {
            Log.d("MainActivity:", "onPositionUpdate: x=$x y=$y")
        }

        override fun onShow() {
            Log.d("MainActivity:", "onShow")
        }

        override fun onHide() {
            Log.d("MainActivity:", "onHide")
        }

        override fun onDismiss() {
            Log.d("MainActivity:", "onDismiss")
        }

        override fun onMoveAnimStart() {
            Log.d("MainActivity:", "onMoveAnimStart")
        }

        override fun onMoveAnimEnd() {
            Log.d("MainActivity:", "onMoveAnimEnd")
        }

        override fun onBackToDesktop() {
            Log.d("MainActivity:", "onBackToDesktop")
        }
    }
}
```
