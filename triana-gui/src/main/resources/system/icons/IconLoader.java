public class IconLoader {

  private Image getImage(String name) {
    Image image = null;

    try {
      int c;
      InputStream in = this.getClass().getResourceAsStream(name);
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      for (;;) {
        if ((c = in.read()) < 0) break;
        out.write(c);
      }

      image = this.getToolkit().createImage(out.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
    }

    return image;
  }
}







