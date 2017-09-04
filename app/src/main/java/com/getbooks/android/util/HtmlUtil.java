package com.getbooks.android.util;

import android.content.Context;

import com.getbooks.android.R;
import com.getbooks.android.model.Highlight;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by marinaracu on 01.09.17.
 */

public class HtmlUtil {


    /**
     * Function modifies input html string by adding extra css,js and font information.
     *
     * @param context     Activity Context
     * @param htmlContent input html raw data
     * @param mBookTitle  Epub book title
     * @return modified raw html string
     */
    public static String getHtmlContent(Context context, String htmlContent, String mBookTitle) {
        String cssPath =
                String.format(context.getString(R.string.css_tag), "file:///android_asset/Style.css");
        String jsPath =
                String.format(context.getString(R.string.script_tag),
                        "file:///android_asset/Bridge.js");
        jsPath =
                jsPath + String.format(context.getString(R.string.script_tag),
                        "file:///android_asset/jquery-1.8.3.js");
        jsPath =
                jsPath + String.format(context.getString(R.string.script_tag),
                        "file:///android_asset/jpntext.js");
        jsPath =
                jsPath + String.format(context.getString(R.string.script_tag),
                        "file:///android_asset/rangy-core.js");
        jsPath =
                jsPath + String.format(context.getString(R.string.script_tag),
                        "file:///android_asset/rangy-serializer.js");
        jsPath =
                jsPath + String.format(context.getString(R.string.script_tag),
                        "file:///android_asset/android.selection.js");
        jsPath =
                jsPath + String.format(context.getString(R.string.script_tag_method_call),
                        "setMediaOverlayStyleColors('#C0ED72','#C0ED72')");
        String toInject = "\n" + cssPath + "\n" + jsPath + "\n</head>";
        htmlContent = htmlContent.replace("</head>", toInject);

        String classes = "andada";

                classes += " textSizeOne";


//        htmlContent = htmlContent.replace("<html ", "<html class=\"" + classes + "\" ");
//        ArrayList<Highlight> highlights = HighLightTable.getAllHighlights(mBookTitle);
//        for (Highlight highlight : highlights) {
//            String highlightStr = highlight.getContentPre() +
//                    "<highlight id=\"" + highlight.getHighlightId() +
//                    "\" onclick=\"callHighlightURL(this);\" class=\"" +
//                    highlight.getType() + "\">" + highlight.getContent() + "</highlight>" + highlight.getContentPost();
//            String searchStr = highlight.getContentPre() +
//                    "" + highlight.getContent() + "" + highlight.getContentPost();
//            htmlContent = htmlContent.replaceFirst(Pattern.quote(searchStr), highlightStr);
//        }
        return htmlContent;
    }
}
