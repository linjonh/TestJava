import org.junit.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class TestJava {
    //    String logEventFile = "L:\\PROJECT\\GPlayer\\adlibrary\\src\\main\\java\\com\\jayl\\gplayer\\ads\\analytics\\LogEventConstant.java";
    String logEventFile = "L:\\PROJECT\\GPlayer\\adlibrary\\src\\main\\java\\com\\jayl\\gplayer\\ads\\analytics\\LogConsoleConstant.java";

    @Test
    public void replaceLogEvent() throws IOException {
        String GplayerModuSrcDir = "L:\\PROJECT\\GPlayer\\Gplayer\\src\\main\\java\\";
        String lightningBrowserDir = "L:\\PROJECT\\GPlayer\\lightningBrowser\\src\\main\\java\\";
        String databaseSrcDir = "L:\\PROJECT\\GPlayer\\database\\src\\main\\java\\";
        String adlibrarySrcDir = "L:\\PROJECT\\GPlayer\\adlibrary\\src\\main";
        String testFile = "C:\\Users\\Jaysen\\Documents\\LogTest.java";
//        String testFile = "L:\\PROJECT\\GPlayer\\Gplayer\\src\\main\\java\\com\\jayl\\gplayer\\MainPresenter.java";

        //real file
        String[] dirs = new String[]{GplayerModuSrcDir, lightningBrowserDir, databaseSrcDir, adlibrarySrcDir};
        for (String dir : dirs) {
            File fileDir = new File(dir);
            loopFile(fileDir, false);
        }
        //test file
//        replaceFileBaseLogText(new File(testFile), logEventFile);
//        replaceConsoleLogText(new File(testFile), logEventFile);

    }

    private void loopFile(File fileDir, boolean isLogEventReplaceText) throws IOException {
        File[] files = fileDir.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isFile()) {
                    if (isLogEventReplaceText) {
                        replaceFileBaseLogText(file, logEventFile);
                    } else {
                        replaceConsoleLogText(file, logEventFile);
                    }
                } else if (file.isDirectory()) {
                    //do recurseve loop
                    loopFile(file, isLogEventReplaceText);
                }
            }
    }

    private void replaceConsoleLogText(File file, String constantSaveFileName) throws IOException {
        System.out.println("file = " + file.getName());
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        RandomAccessFile logEventRAF = new RandomAccessFile(constantSaveFileName, "rw");
        String line = null;
        boolean modified = false;
        while ((line = randomAccessFile.readLine()) != null) {
            final long pointer = randomAccessFile.getFilePointer();
            if (line.contains("Log.") && !line.contains("FirebaseLog.")) {
//                String[] split = line.split("\\+");
                Set<String> fields = new HashSet<>();
                String orignalLine = line;
                int orignalLineLength = line.length();
                String tmp = line;
                line = loopIndexOfDoudleQoute(line, tmp, fields);

//                line = line.replace("\"", "");//?

                for (String next : fields) {//write constant
                    logEventRAF.seek(logEventRAF.length() - 1);
                    logEventRAF.writeBytes(next + "\r\n");
                }
                if (fields.size() > 0)
                    logEventRAF.writeBytes("}");

                if (!line.equals(orignalLine)) {//replace text
                    modified = true;
                    replace(randomAccessFile, pointer, line, orignalLineLength, false);
                }
            }
        }
        if (modified) {
            randomAccessFile.seek(0);
            String readLine;//escape package name
            for (; ; ) {
                readLine = randomAccessFile.readLine();
                if (readLine != null) {
                    if (readLine.startsWith("/*")
                            || readLine.startsWith("*")
                            || readLine.startsWith("package")) {
                        System.out.println("header:" + readLine);
                        continue;
                    }
                    System.out.println("not header：" + readLine + " length:" + readLine.length());
                    break;
                } else {
                    break;
                }
            }
            long filePointer = randomAccessFile.getFilePointer();
            System.out.println("randomAccessFile=" + randomAccessFile.length());
            System.out.println("filePointer before=" + filePointer);
            filePointer = filePointer - readLine.length() - 2;
            System.out.println("filePointer after =" + filePointer);
            String insertContent = "import com.jayl.gplayer.ads.analytics.LogConsoleConstant;";
            replace(randomAccessFile, filePointer, insertContent, readLine.length(), true);
        }
    }

    @Test
    public void testIndex() {
//        String test = "(\"[" + false + "] currentAlbumName is null and parsed is:\")";
        String test = " MyLog.e(\"[\" + i + \"] currentAlbumName is null and parsed is:\" + s);";
        String temp = test;
        loopIndexOfDoudleQoute(test, temp, new HashSet<>());
    }

    private String loopIndexOfDoudleQoute(String line, String tmp, Set<String> fields) {
        int indexOf = tmp.indexOf("\"");
        if (indexOf != -1) {
            if (indexOf + 1 < tmp.length()) {
                tmp = tmp.substring(indexOf + 1);
                int secondIndexOf = tmp.indexOf("\"");
                if (secondIndexOf != -1) {
                    String substring = tmp.substring(0, secondIndexOf);//截取引号里的字符串
                    if (substring.length() > 0 && !substring.equals("/")) {
                        String replace = substring.replace("\n", "")
                                .replace("\\", "")
                                .replace("=", "")
                                .replace("》", "")
                                .replace(">", "")
                                .replace("[", "")
                                .replace("]", "")
                                .replace(" ", "")
                                .replace(";", "")
                                .replace(".", "")
                                .replace("<", "")
                                .replace("#", "")
                                .replace("^", "")
                                .replace("*", "")
                                .replace("|", "")
                                .replace("\\", "")
                                .replace("+", "_")
                                .replace("】", "")
                                .replace("【", "")
                                .replace("-", "")
                                .replace("(", "")
                                .replace(")", "")
                                .replace("=", "")
                                .replace(",", "")
                                .replace(".", "")
                                .replace("!", "")
                                .replace("?", "")
                                .replace("。", "")
                                .replace("，", "")
                                .replace("'", "")
                                .replace("\"", "")
                                .replace("@", "")
                                .replace("%", "")
                                .replace("$", "")
                                .replace("&", "")
                                .replace(":", "");
                        System.out.println("replace: " + replace);
                        if (replace.length() > 0) {
                            String filedName;
//                            if (replace.length() > 30) {
//                                filedName = replace.substring(0, 20);
//                            } else {
                            filedName = replace;
//                            }
                            line = line.replace("\"" + substring + "\"", "LogConsoleConstant." + filedName);
                            String field = "public static final String " + filedName + "=\"" + substring + "\";";
                            System.out.println("field: " + field);
                            fields.add(field);
                        }
                    }
                    if (secondIndexOf + 1 < tmp.length()) {
                        tmp = tmp.substring(secondIndexOf + 1);//next
                        //  2019/1/6 recursive loop
                        line = loopIndexOfDoudleQoute(line, tmp, fields);
                    }
                }
            }
        }
        return line;
    }

    private void replaceFileBaseLogText(File file, String constantSaveFileName) throws IOException {
        System.out.println("file = " + file.getName());
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        RandomAccessFile logEventRAF = new RandomAccessFile(constantSaveFileName, "rw");

        String line = null;
        boolean modified = false;
        while ((line = randomAccessFile.readLine()) != null) {
            final long pointer = randomAccessFile.getFilePointer();
            if (line.contains("FirebaseLog.log")) {
                System.out.println("original line:" + line);
                String[] split = line.split(",");

                String event = split[1].trim()
                        .replace(" ", "_");
                String alterLine = line;
                Set<String> fields = new HashSet<>();
                if (!event.contains("+")) {
                    if (event.startsWith("\"")) {
                        String filedName = event.replace("\"", "");
                        alterLine = line.replace(split[1], "LogEventConstant." + filedName);
                        String field = "public static final String " + filedName + "=" + event + ";";
                        fields.add(field);
                        System.out.println(field);
                    }
                } else {
                    System.out.println("=================================> contain +");
                }
                if (split.length > 2) {
                    String originTag = split[2].trim()
                            .replace(")", "")
                            .replace(";", "");
                    if (!originTag.contains("+")) {
                        String tagTrim = originTag.replace(" ", "_");
                        String tagName = tagTrim.replace("\"", "");
                        if (tagTrim.startsWith("\"") && tagName.length() > 0) {
                            alterLine = alterLine.replace(originTag, "LogEventConstant." + tagName) + "\r\n";
                            String field = "public static final String " + tagName + "=" + tagTrim + ";";
                            fields.add(field);
                            System.out.println(field);
                        }
                    } else {
                        System.out.println("=================================> contain +");
                    }
                }

//                for (String next : fields) {
//                    logEventRAF.seek(logEventRAF.length() - 1);
//                    logEventRAF.writeBytes(next + "\r\n");
//                }
//                if (fields.size() > 0)
//                    logEventRAF.writeBytes("}");
                if (modified = !alterLine.equals(line)) {
                    replace(randomAccessFile, pointer, alterLine, line.length(), false);
                }
//                randomAccessFile.writeBytes(alterLine);
            }
        }
//        fileReader.close();
//        bufferedReader.close();
        if (modified) {
            randomAccessFile.seek(0);
            String readLine = randomAccessFile.readLine();//escape package name
            long filePointer = randomAccessFile.getFilePointer();
            String insertContent = "import com.jayl.gplayer.ads.analytics.LogEventConstant;";
            replace(randomAccessFile, filePointer, insertContent, readLine.length(), true);
        }
        logEventRAF.close();
        randomAccessFile.close();
    }


    /**
     * 实现向指定位置
     * 插入数据
     *
     * @param raf           RandomAccessFile
     * @param points        指针位置
     * @param insertContent 插入内容
     **/
    public static void replace(RandomAccessFile raf, long points, String insertContent, int replaceContentLength,
                               boolean isInsert) {
        try {
            File tmp = File.createTempFile("tmp", null);
//            tmp.deleteOnExit();//在JVM退出时删除

            //创建一个临时文件夹来保存插入点后的数据
            FileOutputStream tmpOut = new FileOutputStream(tmp);
            FileInputStream tmpIn = new FileInputStream(tmp);
            raf.seek(points);
            /**将插入点后的内容读入临时文件夹**/
            byte[] buff = new byte[1024];

            //+++++++++++++++++++++++++++++++++++++++++
            //用于保存临时读取的字节数
            int hasRead = 0;
            //循环读取插入点后的内容
            while ((hasRead = raf.read(buff)) > 0) {
                // 将读取的数据写入临时文件中
                tmpOut.write(buff, 0, hasRead);
            }
            //+++++++++++++++++++++++++++++++++++++++++
            if (!isInsert) {
                //插入需要指定添加的数据
                raf.seek(points - replaceContentLength);//返回原来的插入处
            } else if (isInsert) {
                raf.seek(points);
            }
            //+++++++++++++++++++++++++++++++++++++++++
            //追加需要追加的内容
            int length = insertContent.length();
            raf.writeBytes(insertContent + "\r\n");
            //+++++++++++++++++++++++++++++++++++++++++
            hasRead = tmpIn.read(buff);
            if (hasRead <= 0) {
                if (!isInsert && length < replaceContentLength) {//要是替换的话，要上删除多余的该行未替换的字符
                    int gap = replaceContentLength - length;
                    raf.setLength(points - gap);
                    raf.seek(points - gap);//恢復當前讀寫的位置;
                }
            } else {//有内容回存储追加
                //+++++++++++++++++++++++++++++++++++++++++
                if (!isInsert && length < replaceContentLength) {
                    int gap = replaceContentLength - length;
                    raf.seek(points - gap);
                    raf.setLength(points - gap + tmp.length());//truncate file length
                }
                //最后追加临时文件中的内容
                do {
                    System.out.println("hasRead:" + hasRead);
                    raf.write(buff, 0, hasRead);
                } while ((hasRead = tmpIn.read(buff)) > 0);
                raf.seek(points);//恢復當前讀寫的位置;
            }
            //+++++++++++++++++++++++++++++++++++++++++
            tmp.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}