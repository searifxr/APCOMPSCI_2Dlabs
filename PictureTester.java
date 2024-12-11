import java.awt.Color;

public class PictureTester {
    public static void main(String[] args) {
        Picture beach = new Picture("beach.jpg");

        beach.view(); //calling the view() method displays the picture with the PictureViewer GUI

        testChromakey();
        testSteganography();
    }

    public static void testChromakey() {
        Picture one = new Picture("blue-mark.jpg");
        Picture two = new Picture("moon-surface.jpg");

        one.view(); //show original mustache guy picture
        two.view(); //show the untouched moon's surface pic

        one.chromakey(two, new Color(10, 40, 75), 60); //replace this color if within 60
        one.view();

        // Apply horizontal motion blur with a blur length of 15
        one.motionBlur(15, true); // Horizontal blur
        one.view();

        // Apply vertical motion blur with a blur length of 15
        one.motionBlur(15, false); // Vertical blur
        one.view();

        // Apply image compression with a level of 10
        one.compress(10);
        one.view(); // View the compressed image

        // Test case: check if chromakey and motion blur have both applied
        int pixelBefore = one.getPixel(100, 100).getColor().getRGB();
        one.chromakey(two, new Color(10, 40, 75), 60);
        one.motionBlur(15, true);
        one.motionBlur(15, false);
        one.compress(10); // Compress after applying all effects
        int pixelAfter = one.getPixel(100, 100).getColor().getRGB();

        if (pixelBefore != pixelAfter) {
            System.out.println("Chromakey, Motion Blur, and Compression applied successfully");
        } else {
            System.out.println("Effect application failed");
        }
    }

    public static void testSteganography() {
        Picture msg = new Picture("msg.jpg");
        Picture beach = new Picture("beach.jpg");

        beach.encode(msg); //hide message in beach picture
        beach.view();      //beach w/ hidden message inside, shouldn't look different

        beach.decode().view(); //see the hidden message in the beach picture

        // Apply horizontal motion blur with a blur length of 10
        beach.motionBlur(10, true); // Horizontal blur
        beach.view();

        // Apply vertical motion blur with a blur length of 10
        beach.motionBlur(10, false); // Vertical blur
        beach.view();

        // Apply image compression with a level of 10
        beach.compress(10);
        beach.view(); // View the compressed image

        // Test case: validate if the decoded message matches the original after motion blur
        if (msg.equals(beach.decode())) {
            System.out.println("Steganography, Motion Blur, and Compression successful");
        } else {
            System.out.println("Steganography failed after motion blur and compression");
        }
    }
}
