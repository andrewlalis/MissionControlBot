package api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LaunchResponse {
	private int id;
	private String name;
	@JsonProperty("launch_description")
	private String launchDescription;

	public static class Paged extends PagedResponse<LaunchResponse> {};
}
