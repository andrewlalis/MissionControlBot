package api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public abstract class PagedResponse<T> {
	private List<String> errors;
	@JsonProperty("valid_auth")
	private boolean validAuth;
	private int count;
	private int limit;
	private int total;
	@JsonProperty("last_page")
	private int lastPage;

	private List<T> result;
}
