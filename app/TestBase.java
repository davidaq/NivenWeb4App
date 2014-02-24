import cn.niven.web4app.annotation.*;

@Service("hello")
public class TestBase {

	@Action
	public String hello() {
		return "hi";
	}
}
