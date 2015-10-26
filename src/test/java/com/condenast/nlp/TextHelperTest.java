package com.condenast.nlp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextHelperTest {

    @Test
    public void testFullTrim() throws Exception {
        String s = TextHelper.fullTrim(" a    fdd dd ");
        assertEquals("a fdd dd", s);
    }

    @Test
    public void testCleanMarkdownAndHtml() throws Exception {
        String test = "[View Slideshow](http://www.architecturaldigest" +
                ".com/gallery/adam-levine-hollywood-hills-home-slideshow)\\n\\n\\n\\nAs anyone who has watched even a" +
                " <b>little</b> MTV knows";
        String actual = TextHelper.cleanMarkdownAndHtml(test);
        String expected = "View Slideshow\\n\\n\\n\\nAs anyone who has watched even a little MTV knows";
        assertEquals(expected, actual);
    }

    @Test
    public void testCleanCopilotField() throws Exception {
        String test = "[View Slideshow](http://www.architecturaldigest" +
                ".com/gallery/adam-levine-hollywood-hills-home-slideshow) As anyone who has watched even a" +
                " <b>little</b> MTV knows";
        String actual = TextHelper.cleanCopilotField(test);
        String expected = "As anyone who has watched even a little MTV knows";
        assertEquals(expected, actual);
    }

    @Test
    public void testCleanHtml() throws Exception {
        String test = "<b>little</b> <div class=\"foo\">MTV knows</div> bazzz <hr/> sdfsdf <br> sdfsdf";
        String actual = TextHelper.cleanHtml(test);
        String expected = "little MTV knows bazzz sdfsdf sdfsdf";
        assertEquals(expected, actual);
    }

}