package test;

import cn.niven.web4app.annotation.Action;
import cn.niven.web4app.annotation.Service;

@Service("/")
public class TestService {

	@Action({ "xx=444", "#TestComponent" })
	public String hello(Integer parameter, TestComponent hehe) {
		return "hi" + hehe.test() + "," + parameter;
	}
}
