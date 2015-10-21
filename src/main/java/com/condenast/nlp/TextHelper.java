package com.condenast.nlp;

import org.jsoup.Jsoup;
import org.pegdown.PegDownProcessor;

/**
 * Created by arau on 10/21/15.
 */
public class TextHelper {

    public static final String SPACE_REGEX = "\\s+";
    public static final String ONE_SPACE = " ";
    public static final String VIEW_SLIDESHOW = "View Slideshow";

    public static String fullTrim(final String text) {
        if (null == text) return text;
        return text.trim().replaceAll(SPACE_REGEX, ONE_SPACE);
    }

    public static String cleanMarkdownAndHtml(final String text) {
        if (null == text) return text;
        final PegDownProcessor pegDownProcessor = new PegDownProcessor();
        final String allHtml = pegDownProcessor.markdownToHtml(text);
        return cleanHtml(allHtml);
    }

    public static String cleanHtml(final String html) {
        if (null == html) return html;
        return Jsoup.parse(html).text();
    }

    public static String cleanCopilotField(final String text) {
        if (null == text) return text;
        final String mdHtmlCleaned = cleanMarkdownAndHtml(text);
        String copilotCleaned = mdHtmlCleaned;
        if (mdHtmlCleaned.startsWith(VIEW_SLIDESHOW)) copilotCleaned = mdHtmlCleaned.substring(VIEW_SLIDESHOW.length());
        return copilotCleaned;
    }

}
