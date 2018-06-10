package com.a10miaomiao.bilimiao.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.SearchBoxHistoryAdapter
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.KeyboardUtil
import com.wyt.searchbox.custom.CircularRevealAnim
import com.wyt.searchbox.db.SearchHistoryDB
import kotlinx.android.synthetic.main.dialog_search_box.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.util.*
import java.util.regex.Pattern

/**
 * Created by 10喵喵 on 2017/10/8.
 */
class SearchBoxFragment : DialogFragment()
        , DialogInterface.OnKeyListener
        , CircularRevealAnim.AnimListener
        , ViewTreeObserver.OnPreDrawListener {

    var onSearchClick: ((searchKey: String) -> Boolean)? = null

    //动画
    private var mCircularRevealAnim: CircularRevealAnim? = null
    //历史搜索记录
    private var allHistorys = ArrayList<String>()
    private val historys = ArrayList<String>()
    //适配器
    private var searchHistoryAdapter: SearchBoxHistoryAdapter? = null
    //数据库
    private val searchHistoryDB: SearchHistoryDB by lazy {
        SearchHistoryDB(context, SearchHistoryDB.DB_NAME, null, 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle2)
    }

    override fun onStart() {
        super.onStart()
        initDialog()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定
        var view = inflater?.inflate(R.layout.dialog_search_box, container, false)
        init()//实例化
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_search_back.setOnClickListener {
            hideAnim()
        }
        iv_search_search.setOnClickListener {
            KeyboardUtil.closeKeyboard(activity, et_search_keyword)
            hideAnim()
            search()
        }
        view_search_outside.setOnClickListener {
            hideAnim()
        }
        iv_search_search.viewTreeObserver.addOnPreDrawListener(this)//绘制监听
        allHistorys = searchHistoryDB.queryAllHistory()
        setAllHistorys()

        //初始化recyclerView
        searchHistoryAdapter = SearchBoxHistoryAdapter(historys)
        rv_search_history.layoutManager = LinearLayoutManager(context)//list类型
        rv_search_history.adapter = searchHistoryAdapter
        //设置删除单个记录的监听
        searchHistoryAdapter?.setOnItemClickListener({ baseQuickAdapter, view, i ->
            onItemClick(historys[i])
        })
        searchHistoryAdapter?.setOnItemLongClickListener({ baseQuickAdapter, view, i ->
            if( tv_search_clean!!.visibility == View.GONE)
                return@setOnItemLongClickListener true
            AlertDialog.Builder(activity)
                    .setTitle("确定删除：\"${historys[i]}\"")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", { dialogInterface, ii ->
                        onItemDeleteClick(historys[i])
                    })
                    .show()
            true
        })

        KeyboardUtil.openKeyboard(activity, et_search_keyword)
        tv_search_clean.setOnClickListener {
            searchHistoryDB.deleteAllHistory()
            historys.clear()
            tv_search_clean.visibility = View.GONE
            searchHistoryAdapter?.notifyDataSetChanged()
        }

        //监听编辑框文字改变
        et_search_keyword.addTextChangedListener(TextWatcherImpl())

        if (arguments.containsKey(ConstantUtil.KETWORD)) {
            et_search_keyword.setText(arguments.getString(ConstantUtil.KETWORD))
            et_search_keyword.setSelection(et_search_keyword.text.length)
        }
    }

    private fun init() {
        //实例化动画效果
        mCircularRevealAnim = CircularRevealAnim()
        //监听动画
        mCircularRevealAnim?.setAnimListener(this)
        dialog.setOnKeyListener(this)//键盘按键监听
    }

    private fun setAllHistorys() {
        historys.clear()
        historys.addAll(allHistorys)
        checkHistorySize()
    }

    private fun isID(keyword: String): Boolean{
        val compile = Pattern.compile("\\d+")
        val matcher = compile.matcher(keyword)
        if (matcher.find()){
            historys.clear()
            arrayOf("av", "ss", "live", "au", "cv", "ep").mapTo(historys){
                "$it$keyword"
            }
            tv_search_clean?.visibility = View.GONE
            searchHistoryAdapter?.isHis = false
            searchHistoryAdapter?.notifyDataSetChanged()
            return true
        }
        return false
    }
    private fun setKeyWordHistorys(keyword: String) {
        if(isID(keyword))
            return
        MiaoHttp.newStringClient(
                url = BiliApiService.getKeyWord(keyword),
                onResponse = {
                    if(keyword != et_search_keyword?.text.toString())
                        return@newStringClient
                    val jsonParser = JSONTokener(it)
                    historys.clear()
                    try {
                        val jsonArray = (jsonParser.nextValue() as JSONObject).getJSONObject("result").getJSONArray("tag")
                        (0 until jsonArray.length()).mapTo(historys) { jsonArray.getJSONObject(it).getString("value") }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }
                    tv_search_clean?.visibility = View.GONE
                    searchHistoryAdapter?.isHis = false
                    searchHistoryAdapter?.notifyDataSetChanged()
                }
        )
    }

    private fun checkHistorySize() {
        if (historys.size < 1) {
            tv_search_clean?.visibility = View.GONE
        } else {
            tv_search_clean?.visibility = View.VISIBLE
        }
    }

    /**
     * 初始化SearchFragment
     */
    private fun initDialog() {
        val window = dialog.window
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels //DialogSearch的宽
        window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT)
        window.setGravity(Gravity.TOP)
        window.setWindowAnimations(R.style.DialogEmptyAnimation)//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    private fun hideAnim() {
        mCircularRevealAnim?.hide(iv_search_search, view)
    }

    private fun search() {
        val searchKey = et_search_keyword.text.toString()
        if (searchKey == null || searchKey.isEmpty()) {
            Toast.makeText(context, "请输入ID或关键字", Toast.LENGTH_SHORT).show()
        } else {
            if (onSearchClick != null) {
                if (onSearchClick!!.invoke(searchKey)) {
                    searchHistoryDB.deleteHistory(searchKey)
                    searchHistoryDB.insertHistory(searchKey)
                }
            }
        }
    }

    /**
     * 监听键盘按键
     */
    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            hideAnim()
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            hideAnim()
            search()
        }
        return false
    }

    /**
     * 监听搜索键绘制时
     */
    override fun onPreDraw(): Boolean {
        iv_search_search.viewTreeObserver.removeOnPreDrawListener(this)
        mCircularRevealAnim?.show(iv_search_search, view)
        return true
    }

    /**
     * 搜索框动画显示完毕时调用
     */
    override fun onShowAnimationEnd() {
        if (isVisible) {
            KeyboardUtil.openKeyboard(activity, et_search_keyword)
        }
    }

    /**
     * 搜索框动画隐藏完毕时调用
     */
    override fun onHideAnimationEnd() {
        KeyboardUtil.closeKeyboard(activity, et_search_keyword)
        dismiss()
    }

    /**
     * 删除单个搜索记录
     */
    private fun onItemDeleteClick(keyword: String?) {
        searchHistoryDB.deleteHistory(keyword)
        historys.remove(keyword)
        searchHistoryAdapter?.notifyDataSetChanged()
        checkHistorySize()
    }

    /**
     * 点击单个搜索记录
     */
    private fun onItemClick(keyword: String?) {
        KeyboardUtil.closeKeyboard(activity, et_search_keyword)
        searchHistoryDB.deleteHistory(keyword)
        searchHistoryDB.insertHistory(keyword)
        hideAnim()
        onSearchClick?.invoke(keyword!!)
    }

    /**
     * 监听编辑框文字改变
     */
    private inner class TextWatcherImpl : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }
        override fun afterTextChanged(editable: Editable) {
            val keyword = editable.toString()
            if (TextUtils.isEmpty(keyword.trim { it <= ' ' })) {
                setAllHistorys()
                searchHistoryAdapter?.isHis = true
                searchHistoryAdapter?.notifyDataSetChanged()
            } else {
                setKeyWordHistorys(editable.toString())
                searchHistoryAdapter?.notifyDataSetChanged()
            }
        }
    }


    companion object {
        fun newInstance(): SearchBoxFragment {
            val bundle = Bundle()
            val searchFragment = SearchBoxFragment()
            searchFragment.arguments = bundle
            return searchFragment
        }

        fun newInstance(keyword: String): SearchBoxFragment {
            val fragment = SearchBoxFragment()
            val bundle = Bundle()
            bundle.putString(ConstantUtil.KETWORD, keyword)
            fragment.arguments = bundle
            return fragment
        }
    }
}

