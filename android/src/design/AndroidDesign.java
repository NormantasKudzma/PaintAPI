package design;

public class AndroidDesign extends PcDesign{
	public AndroidDesign() {
		super();
		new CommServer().start();
	}
}
