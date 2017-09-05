package com.getbooks.android.skyepubreader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.skytree.epub.Book;
import com.skytree.epub.ContentProvider;
import com.skytree.epub.ContentData;
import com.skytree.epub.KeyListener;
import com.skytree.epub.i;
import com.skytree.epub.y;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class EpubProvider implements ContentProvider {
//	ZipFile zipFile = null;
//	private void debug(String msg) {
//		Log.w("EPub",msg);
//	}
//
//	private boolean isCustomFont(String contentPath) {
//		if (contentPath.startsWith("/fonts")) {
//			return true;
//		}
//		return false;
//	}
//
//
//	private void setupZipFile(String baseDirectory,String contentPath) {
//		if (zipFile!=null) return;
//		String[] subDirs = contentPath.split(Pattern.quote(File.separator));
//		String fileName = subDirs[1]+".epub";
//		String filePath = baseDirectory+"/"+subDirs[1]+"/"+fileName;
//		try {
////			InputStream inputStream = Decryption.decryptStream(filePath, fileName);
////			byte[] buffer = new byte[inputStream.available()];
////			inputStream.read(buffer);
//			File file = new File(filePath);
////			OutputStream outStream = new FileOutputStream(file);
////			outStream.write(buffer);
//			zipFile = new ZipFile(file);
//		}catch(Exception getNumber) {
//			zipFile=null;
//		}
//	}
//
//	// Entry name should start without / like META-INF/container.xml
//	private ZipEntry getZipEntry(String contentPath) {
//		if (zipFile==null) return null;
//		String[] subDirs = contentPath.split(Pattern.quote(File.separator));
//		String corePath = contentPath.replace(subDirs[1], "");
//		corePath=corePath.replace("//", "");
//		ZipEntry entry = zipFile.getEntry(corePath.replace(File.separatorChar, '/'));
//		return entry;
//	}
//
//	public boolean isExists(String baseDirectory,String contentPath) {
//		setupZipFile(baseDirectory,contentPath);
//		if (this.isCustomFont(contentPath)) {
//			String path = baseDirectory +"/"+ contentPath;
//			File file = new File(path);
//			return file.exists();
//		}
//
//		ZipEntry entry = this.getZipEntry(contentPath);
//		if (entry==null) {
////			Log.w("EPub",contentPath+" not exist");
//		}
//		if (contentPath.contains("mp4")) {
////			Log.w("EPub",contentPath);
//		}
//		if (entry==null) return false;
//		else return true;
//	}
//
//
//	public ContentData getContentData(String baseDirectory,String contentPath) {
//		debug("getInputStream "+contentPath);
//		ContentData data = new ContentData();
//		data.contentPath = contentPath;
//		if (this.isCustomFont(contentPath)) {
//			String path = baseDirectory + "/" + contentPath;
//			FileInputStream fis = null;
//			File file = new File(path);
//			long fileLength = file.length();
//			try {
//				fis = new FileInputStream(file);
//			}catch(Exception getNumber) {}
//			data.contentLength = fileLength;
//			data.inputStream = fis;
//			return data;
//		}
//		InputStream is = null;
//		try {
//			ZipEntry entry = this.getZipEntry(contentPath);
//			if (entry==null) return null;
//			is = zipFile.getInputStream(entry);
//			long length = entry.getSize();
//			// in some zip format, zipEntry can't generates proper inputStream,
//			// to fix this, byteArrayInputStream is used instead of zipEntry.getInputStream.
//			if (is.available()==1) {
//				BufferedInputStream bis = new BufferedInputStream(is);
//				int file_size  = (int) entry.getCompressedSize();
//				byte[] blob = new byte[(int) entry.getCompressedSize()];
//				int bytes_read = 0;
//				int offset = 0;
//				while((bytes_read = bis.read(blob, 0, file_size)) != -1) {
//					offset += bytes_read;
//				}
//				bis.close();
//				ByteArrayInputStream bas = new ByteArrayInputStream(blob);
//				is = bas;
//				length = is.available();
//			}
//			data.contentLength = length;
//			data.inputStream = is;
//			data.lastModified = entry.getTime();
//			return data;
//		}catch(Exception getNumber) {
//			debug(getNumber.getMessage());
//			getNumber.printStackTrace();
//		}
//		return null;
//	}

    private final String g = "com.skytree.epub.SKYERROR";
    ZipFile zipFile = null;
    KeyListener mKeyListener = null;
    Book book;
    Context context;
    boolean ifKeyTest = false;
    String stringTest = "test";

    private void createSecretKey(int codeError, int levelError, String massegeError) {
        if (this.context != null) {
            Intent intent = new Intent("com.skytree.epub.SKYERROR");
            intent.putExtra("code", codeError);
            intent.putExtra("level", levelError);
            intent.putExtra("message", massegeError);
            this.context.sendBroadcast(intent);
        }

    }

    public EpubProvider() {
        this.createSecretKey("SkyProvider Created");
    }

    public void setBook(Book book1) {
        this.book = book1;
    }

    public void setKeyListener(KeyListener keyListener) {
        this.mKeyListener = keyListener;
    }

    public void setContext(Context context1) {
        this.context = context1;
    }

    private void createSecretKey(String string) {
        Log.w("EpubProvider", string);
    }

    private boolean checkIsFontDirectory(String directory) {
        return directory.startsWith("/fonts");
    }

    private void createSecretKey(String directoryPath, String contentPath) {
        if (this.zipFile == null) {
            if (directoryPath != null && !directoryPath.isEmpty()) {
                String[] var3 = contentPath.split(Pattern.quote(File.separator));
                String book = var3[1] + ".epub";
                String bookPath = directoryPath + File.separator + book;
                String allPathBook = directoryPath + File.separator + var3[1] + File.separator + book;
                String newBookPath = "";
                File file = new File(bookPath);
                if (file.exists()) {
                    newBookPath = bookPath;
                } else {
                    file = new File(allPathBook);
                    if (file.exists()) {
                        newBookPath = allPathBook;
                    }
                }

                if (newBookPath.isEmpty()) {
                    this.createSecretKey(2, 1, "EPub file " + book + " is not fount at " + bookPath + " or " + allPathBook);
                } else {
                    try {
                        file = new File(newBookPath);
                        this.zipFile = new ZipFile(file);
                        return;
                    } catch (Exception var13) {
                        this.zipFile = null;
                    } finally {
                        if (this.zipFile == null) {
                            this.createSecretKey(3, 1, "EPub file " + book + " is corrupted or invalied.");
                        }

                    }

                }
            } else {
                this.createSecretKey(1, 1, "BaseDirectory is not set or empty");
            }
        }
    }

    private ZipEntry getZipEntry(String contentPath) {
        if (this.zipFile == null) {
            return null;
        } else {
            String[] subDirs = contentPath.split(Pattern.quote(File.separator));
            String corePath = contentPath.replace(subDirs[1], "");
            corePath = corePath.replace("//", "");
            corePath = corePath.replace(File.separatorChar, '/');
            ZipEntry zipEntry = this.zipFile.getEntry(corePath);
            return zipEntry;
        }
    }

    public boolean isExists(String baseDirectory, String contentPath) {
        this.createSecretKey(baseDirectory, contentPath);
        if (this.checkIsFontDirectory(contentPath)) {
            String path = baseDirectory + "/" + contentPath;
            File file = new File(path);
            return file.exists();
        } else {
            ZipEntry var3 = this.getZipEntry(contentPath);
            contentPath.contains("mp4");
            return var3 != null;
        }
    }

    private String getKey(String contentPath) {
        if (this.mKeyListener == null) {
            Log.w("EpubProvider", "mKeyListener == null");
            return null;
        } else {
            if (this.book == null) {
                Log.w("EpubProvider", "this.book == null");
                this.book = this.mKeyListener.getBook();
            }

            if (this.book != null && this.book.encryption != null) {
                for (int var2 = 0; var2 < this.book.encryption.b.size(); ++var2) {
                    y var3 = (y) this.book.encryption.b.get(var2);
                    String var4 = var3.d.a.toLowerCase();
                    var4 = var4.replace("\\", "/");
                    if (contentPath.toLowerCase().endsWith(var4)) {
                        if (this.mKeyListener != null) {
                            String key = "";
                            if (this.ifKeyTest) {
                                key = this.stringTest;
                                Log.w("EpubProvider", key);
                            } else {
                                key = this.mKeyListener.getKeyForEncryptedData
                                        (var3.c.a, var3.d.a, this.book.encryption.a);
                                Log.w("EpubProvider", key);
                            }

                            return key;
                        }
                        Log.w("EpubProvider", "return key null");
                        return null;
                    }
                }
            }

            return null;
        }
    }

    private int getNumber(String var1) {
        if (this.mKeyListener == null) {
            return 128;
        } else {
            if (this.book == null) {
                this.book = this.mKeyListener.getBook();
            }

            if (this.book != null && this.book.encryption != null) {
                for (int i = 0; i < this.book.encryption.b.size(); ++i) {
                    y var3 = (y) this.book.encryption.b.get(i);
                    String var4 = var3.d.a.toLowerCase();
                    var4 = var4.replace("\\", "/");
                    if (var1.toLowerCase().endsWith(var4)) {
                        if (var3.b.a.contains("aes128")) {
                            return 128;
                        }

                        if (var3.b.a.contains("aes192")) {
                            return 192;
                        }

                        if (var3.b.a.contains("aes256")) {
                            return 256;
                        }

                        return 128;
                    }
                }
            }

            return 128;
        }
    }

    private static SecretKeySpec createSecretKey(String var0, int var1) throws UnsupportedEncodingException {
        Log.w("EpubProvider", "createSecretKey");
        byte[] var2 = new byte[var1 / 8];
        Arrays.fill(var2, (byte) 0);
        byte[] var3 = var0.getBytes("UTF-8");
        int var4 = var3.length < var2.length ? var3.length : var2.length;
        System.arraycopy(var3, 0, var2, 0, var4);
        SecretKeySpec secretKeySpec = new SecretKeySpec(var2, "AES");
        return secretKeySpec;
    }

    public ContentData getContentData(String baseDirectory, String contentPath) {
        Log.w("EpubProvider", "getContentData");
        String key = this.getKey(contentPath);
        ContentData contentData = new ContentData();
        contentData.contentPath = contentPath;
        if (this.checkIsFontDirectory(contentPath)) {
            String var22 = baseDirectory + "/" + contentPath;
            FileInputStream var21 = null;
            File var23 = new File(var22);
            long var8 = var23.length();

            try {
                var21 = new FileInputStream(var23);
            } catch (Exception var18) {
                ;
            }

            contentData.contentLength = var8;
            contentData.inputStream = var21;
            return contentData;
        } else {
            InputStream var5 = null;
            try {
                Log.w("EpubProvider", "getContentData");
                ZipEntry var6 = this.getZipEntry(contentPath);
                if (var6 == null) {
                    return null;
                } else {
                    Log.w("EpubProvider", "-----");
                    var5 = this.zipFile.getInputStream(var6);
                    long var7 = var6.getSize();
                    if (var7 > 47185920L) {
                        return null;
                    } else {
                        i var9 = new i();
                        byte[] var11 = new byte[16384];

                        int var10;
                        while ((var10 = var5.read(var11, 0, var11.length)) != -1) {
                            var9.write(var11, 0, var10);
                        }

                        var9.flush();
                        int var12;
                        ByteArrayInputStream byteArrayInputStream;
                        if (key != null) {
                            Log.w("EpubProvider", "=========");
                            var12 = this.getNumber(contentPath);
                            SecretKeySpec var13 = createSecretKey(key, var12);
                            byte[] var14 = new byte[16];
                            Arrays.fill(var14, (byte) 0);
                            IvParameterSpec var15 = new IvParameterSpec(var14);
                            Cipher var16 = Cipher.getInstance("AES/CBC/PKCS5Padding");
                            var16.init(2, var13, var15);
                            byte[] var17 = var16.doFinal(var9.a());
                            byteArrayInputStream = new ByteArrayInputStream(var17);
                            var7 = (long) var17.length;
                        } else {
                            var12 = var9.a().length;
                            if (var7 != (long) var12) {
                                var7 = (long) var12;
                            }

                            byteArrayInputStream = new ByteArrayInputStream(var9.a());
                        }

                        contentData.contentLength = var7;
                        contentData.inputStream = byteArrayInputStream;
                        contentData.lastModified = var6.getTime();
                        return contentData;
                    }
                }
            } catch (Exception error) {
                this.createSecretKey(error.getMessage());
                error.printStackTrace();
                return null;
            }
        }
    }

    protected void finalize() throws Throwable {
        try {
            this.createSecretKey("SkyProvider finalize() called");
            this.zipFile = null;
            this.mKeyListener = null;
            this.book = null;
        } finally {
            super.finalize();
        }

    }


}