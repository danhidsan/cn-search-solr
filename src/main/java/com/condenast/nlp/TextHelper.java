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
    public static final String VIEW_SLIDESHOW_MD = "[View Slideshow]";

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
        String copilotCleaned = cleanMarkdownAndHtml(text);
        copilotCleaned = cleanSpecialSlideshowMD(text, copilotCleaned);
        copilotCleaned = cleanSpecialBracketsMD(copilotCleaned);
        copilotCleaned = cleanPipes(copilotCleaned);
        return copilotCleaned.trim();
    }

    private static String cleanSpecialSlideshowMD(String text, String copilotCleaned) {
        if (text.startsWith(VIEW_SLIDESHOW_MD) && copilotCleaned.startsWith(VIEW_SLIDESHOW)) {
            copilotCleaned = copilotCleaned.substring(VIEW_SLIDESHOW.length() + 1);
        }
        return copilotCleaned;
    }

    public static String cleanSpecialBracketsMD(final String text) {
        String ret = text;
        String tmp;
        while (true) {
            tmp = cleanSpecialBracketsMDInner(ret);
            if (tmp == null) break;
            ret = tmp;
        }
        return ret;
    }

    private static String cleanSpecialBracketsMDInner(String text) {
        int start = text.indexOf("[#");
        if (start < 0) return null;
        int end = text.indexOf("]", start);
        if (end < 0) return null;
        String cleanedLeft = text.substring(0, start);
        String cleanedRight = text.substring(end + 1);
        String cleaned = cleanedLeft.concat(cleanedRight);
        return cleaned.trim();
    }

    public static String cleanPipes(final String text) {
        String ret = text;
        String tmp;
        while (true) {
            tmp = cleanPipesInner(ret);
            if (tmp == null) break;
            ret = tmp;
        }
        return ret;
    }

    private static String cleanPipesInner(final String text) {
        int startOfPipes = text.indexOf('|');
        if (startOfPipes < 0) return null;
        int endOfPipes;
        for (endOfPipes = startOfPipes; endOfPipes < text.length(); endOfPipes++) {
            if (text.charAt(endOfPipes) != '|') break;
        }
        if (endOfPipes - startOfPipes <= 1) return null;
        String cleanedLeft = text.substring(0, startOfPipes).trim();
        String cleanedRight = text.substring(endOfPipes);
        String cleaned = cleanedLeft.concat(cleanedRight);
        return cleaned.trim();
    }

}
