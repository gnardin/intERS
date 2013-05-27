package intERS.conf.simulation;

public class OutputConf {

	private String directory;
	private String fileExtorter;
	private String fileObserver;
	private String fileTarget;
	private Boolean fileAppend;
	private String fieldSeparator;
	private int writeEvery;

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getFileExtorter() {
		return fileExtorter;
	}

	public void setFileExtorter(String fileExtorter) {
		this.fileExtorter = fileExtorter;
	}

	public String getFileObserver() {
		return fileObserver;
	}

	public void setFileObserver(String fileObserver) {
		this.fileObserver = fileObserver;
	}

	public String getFileTarget() {
		return fileTarget;
	}

	public void setFileTarget(String fileTarget) {
		this.fileTarget = fileTarget;
	}

	public Boolean getFileAppend() {
		return fileAppend;
	}

	public void setFileAppend(Boolean fileAppend) {
		this.fileAppend = fileAppend;
	}

	public String getFieldSeparator() {
		return fieldSeparator;
	}

	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public int getWriteEvery() {
		return writeEvery;
	}

	public void setWriteEvery(int writeEvery) {
		this.writeEvery = writeEvery;
	}

	@Override
	public String toString() {
		String str = new String();

		str += "OUTPUT\n";
		str += "Directory....................: [" + this.directory + "]\n";
		str += "Filename Extorter............: [" + this.fileExtorter + "]\n";
		str += "Filename Observer............: [" + this.fileObserver + "]\n";
		str += "Filename Target..............: [" + this.fileTarget + "]\n";
		str += "File Append..................: [" + this.fileAppend + "]\n";
		str += "Field Separator..............: [" + this.fieldSeparator + "]\n";
		str += "Write Every..................: [" + this.writeEvery + "]\n";

		return str;
	}
}