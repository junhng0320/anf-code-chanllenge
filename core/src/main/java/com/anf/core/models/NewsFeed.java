package com.anf.core.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsFeed {
	
	List<News> articles = new ArrayList<>();
	
	@SlingObject
    private ResourceResolver resourceResolver;
	
	@PostConstruct
	private void init() {
		Resource newsList = resourceResolver.getResource("/var/commerce/products/anf-code-challenge/newsData");
		final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
		String date = dateFormat.format(new Date());
		if(null != newsList) {
			for(Resource newsResource:newsList.getChildren()) {
				News news = new News();
				news.setAuthor(newsResource.getValueMap().get("author", ""));
				news.setContent(newsResource.getValueMap().get("content", ""));
				news.setDescription(newsResource.getValueMap().get("description", ""));
				news.setTitle(newsResource.getValueMap().get("title", ""));
				news.setUrl(newsResource.getValueMap().get("url", ""));
				news.setUrlImage(newsResource.getValueMap().get("urlImage", ""));
				news.setDate(date);
				articles.add(news);
			}
		}
		
		
	}
	public final List<News> getArticles() {
		return articles;
	}
	
	public class News {
				
		String author;
		String content;
		String description;
		String title;
		String url;
		public String getAuthor() {
			return author;
		}
		public void setAuthor(String author) {
			this.author = author;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getUrlImage() {
			return urlImage;
		}
		public void setUrlImage(String urlImage) {
			this.urlImage = urlImage;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		String urlImage;
		String date;

	}


}
