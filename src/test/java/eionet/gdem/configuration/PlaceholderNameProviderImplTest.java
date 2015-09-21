/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.configuration;

import java.util.Set;
import org.junit.Test;
import static junit.framework.TestCase.assertTrue;

public class PlaceholderNameProviderImplTest {

    @Test
    public void testThatMultiplePlaceholdersAreCorrectlyExtracted() {
        PlaceholderNameProviderImpl provider = new PlaceholderNameProviderImpl();
        Set<String> expected = provider.extract("${a}/${b}/${c}");
        assertTrue(expected.size() == 3);
        assertTrue(expected.contains("a"));
        assertTrue(expected.contains("b"));
        assertTrue(expected.contains("c"));

    }

    @Test
    public void testThatNoDuplicatePlacehoders() {
        PlaceholderNameProviderImpl provider = new PlaceholderNameProviderImpl();
        Set<String> expected = provider.extract("${a}/${a}/${b}/${b}");
        assertTrue(expected.size() == 2);
        assertTrue(expected.contains("a"));
        assertTrue(expected.contains("b"));

    }

}
