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

        test = "[View Slideshow](http://www.architecturaldigest" +
                ".com/gallery/adam-levine-hollywood-hills-home-slideshow) |||| As anyone who has watched even a" +
                " <b>little</b> MTV knows";
        actual = TextHelper.cleanCopilotField(test);
        expected = "As anyone who has watched even a little MTV knows";
        assertEquals(expected, actual);

    }

    @Test
    public void testCleanHtml() throws Exception {
        String test = "<b>little</b> <div class=\"foo\">MTV knows</div> bazzz <hr/> sdfsdf <br> sdfsdf";
        String actual = TextHelper.cleanHtml(test);
        String expected = "little MTV knows bazzz sdfsdf sdfsdf";
        assertEquals(expected, actual);
    }

    @Test
    public void testCleanSpecialMD() throws Exception {
        String test = "lorem ipsum foo [#gallery: /galleries/55e78b62cd709ad62e8fea79]|||||| baz hiphop.";
        String actual = TextHelper.cleanSpecialBracketsMD(test);
        String expected = "lorem ipsum foo |||||| baz hiphop.";
        assertEquals(expected, actual);

        test = "[#gallery: /galleries/55e78b62cd709ad62e8fea79]||||||";
        actual = TextHelper.cleanSpecialBracketsMD(test);
        expected = "||||||";
        assertEquals(expected, actual);

        test = "[#gallery: /galleries/55e78b62cd709ad62e8fea79]|||||| baz hiphop.";
        actual = TextHelper.cleanSpecialBracketsMD(test);
        expected = "|||||| baz hiphop.";
        assertEquals(expected, actual);

        test = "lorem ipsum foo [#gallery: /galleries/55e78b62cd709ad62e8fea79]||||||";
        actual = TextHelper.cleanSpecialBracketsMD(test);
        expected = "lorem ipsum foo ||||||";
        assertEquals(expected, actual);

    }

    @Test
    public void testCleanSpecialMDRepeated() throws Exception {
        String test = "lorem ipsum foo [#gallery: /galleries/55e78b62cd709ad62e8fea79]|||||| baz hiphop. [#gallery: /galleries/55e78b62cd709ad62e8fea79]";
        String actual = TextHelper.cleanSpecialBracketsMD(test);
        String expected = "lorem ipsum foo |||||| baz hiphop.";
        assertEquals(expected, actual);
    }

        @Test
    public void testCleanPipes() throws Exception {
        String test = "lorem ipsum foo |||||| baz hiphop.";
        String actual = TextHelper.cleanPipes(test);
        String expected = "lorem ipsum foo baz hiphop.";
        assertEquals(expected, actual);

        test = "||||||";
        actual = TextHelper.cleanPipes(test);
        expected = "";
        assertEquals(expected, actual);

        test = "||";
        actual = TextHelper.cleanPipes(test);
        expected = "";
        assertEquals(expected, actual);

        test = "|";
        actual = TextHelper.cleanPipes(test);
        expected = "|";
        assertEquals(expected, actual);

        test = "|||||| baz hiphop.";
        actual = TextHelper.cleanPipes(test);
        expected = "baz hiphop.";
        assertEquals(expected, actual);

        test = "lorem ipsum foo ||||||.";
        actual = TextHelper.cleanPipes(test);
        expected = "lorem ipsum foo.";
        assertEquals(expected, actual);


    }


}