package com.anf.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class NewsFeedTest {
	
	private static final String JSON_PATH = "/com/anf/core/models/NewsFeed.json";	
	private static final String DEST_PATH = "/var/commerce/products/anf-code-challenge";
	private static final String CURRENT_RESOURCE_PATH = "/var/commerce/products/anf-code-challenge/newsFeed";
	
	private final AemContext ctx = new AemContext();
	
	@InjectMocks
	NewsFeed newsFeed;
	
	@BeforeEach
    void setUp() throws Exception {
    	ctx.addModelsForClasses(NewsFeed.class);
    	ctx.load().json(JSON_PATH, DEST_PATH);
    	newsFeed = ctx.currentResource(CURRENT_RESOURCE_PATH).adaptTo(NewsFeed.class);
	}
	
	@Test
	void testArtciles() {
		assertEquals(10, newsFeed.getArticles().size());
	}

}
