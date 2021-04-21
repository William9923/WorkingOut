package com.softhouse.workingout.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.softhouse.workingout.R

private const val ARG_PARAM1 = "param1"

class WebFragment : Fragment() {
    private var url: String? = null

    private val args: WebFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(ARG_PARAM1)
        }

        // Invoke trigger for appbar menu
        setHasOptionsMenu(true)

        // Setup url for webview
        url = args.url
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            // Overriding back button -> return to previous stack
//            android.R.id.home -> {
//                activity?.onBackPressed()
//                requireActivity().
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView = view.findViewById(R.id.webview)

        webView.loadUrl(url)
        webView.webViewClient = WebViewClient()
        with(webView.settings) {
            javaScriptEnabled = true
        }

    }

    companion object
}