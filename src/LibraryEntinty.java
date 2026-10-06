public interface LibraryEntinty {
    public String toFileString();
    public static <T extends LibraryEntinty> T fromFileString(String fileString){
        return null;
    };
}
