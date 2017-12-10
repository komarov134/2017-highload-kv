package ru.mail.polis.neron;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

/**
 * @author neron
 */
public class MyFileDAO implements MyDAO {

  @NotNull
  private final File dir;

  @NotNull
  private File getFile(@NotNull final String key) {
    return new File(dir, key);
  }

  public MyFileDAO(@NotNull final File dir) {
    this.dir = dir;
  }

  @NotNull
  @Override
  public byte[] get(@NotNull final String key) throws NoSuchElementException, IllegalArgumentException, IOException {
    final File file = getFile(key);
    if (!file.exists()) {
      throw new NoSuchElementException();
    }
    final byte[] value = new byte[(int) file.length()];
    try(InputStream is = new FileInputStream(file)) {
      if (is.read(value) != value.length) {
        throw new IOException("Can't read file" + file.getName());
      }
    }
    return value;
  }

  @Override
  public void upsert(
      @NotNull final String key,
      @NotNull final byte[] value) throws IllegalArgumentException, IOException {
    try(OutputStream os = new FileOutputStream(getFile(key))) {
      os.write(value);
    }
  }

  @Override
  public void delete(@NotNull final String key) throws IllegalArgumentException, IOException {
    getFile(key).delete();
  }
}
