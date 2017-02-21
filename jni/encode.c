#include <android/log.h>
#define LOG_TAG "debug"
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)

/********************************************************************
	    created:    2012/02/07
	    filename:   myfb.c
	    author:

	    purpose:
 *********************************************************************/
#ifndef WIN32
//-------------------------------------------------------------------

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <linux/fb.h>
#include <linux/kd.h>

#include <memory.h>
#include <jni.h>
#include "libavutil/opt.h"
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libavutil/imgutils.h"
#include "libavutil/pixfmt.h"

int rgb_to_yuv(int w,int h,uint8_t *p);
//int rgb_to_yuv(int w,int h);
int flush_encoder(AVFormatContext *fmt_ctx,unsigned int stream_index);
int ffmpeg_encode(int h,int w);
struct FB {
	unsigned short *bits;
	unsigned size;
	int fd;
	struct fb_fix_screeninfo fi;
	struct fb_var_screeninfo vi;
};
int len=0;
int fb_bpp(struct FB *fb)
{
	if (fb) {
		return fb->vi.bits_per_pixel;
	}
	return 0;
}
int fb_width(struct FB *fb)
{
	if (fb) {
		return fb->vi.xres;
	}
	return 0;
}

int fb_height(struct FB *fb)
{
	if (fb) {
		return fb->vi.yres;
	}
	return 0;
}

int fb_size(struct FB *fb)
{
	if (fb) {
		unsigned bytespp = fb->vi.bits_per_pixel / 8;
		return (fb->vi.xres * fb->vi.yres * bytespp);
	}
	return 0;
}

int fb_virtual_size(struct FB *fb)
{
	if (fb) {
		unsigned bytespp = fb->vi.bits_per_pixel / 8;
		return (fb->vi.xres_virtual * fb->vi.yres_virtual * bytespp);
	}
	return 0;
}

void * fb_bits(struct FB *fb)
{
	unsigned short * bits = NULL;
	if (fb) {
		int offset, bytespp;
		bytespp = fb->vi.bits_per_pixel / 8;

		/* HACK: for several of our 3d cores a specific alignment
		 * is required so the start of the fb may not be an integer number of lines
		 * from the base.  As a result we are storing the additional offset in
		 * xoffset. This is not the correct usage for xoffset, it should be added
		 * to each line, not just once at the beginning */
		offset = fb->vi.xoffset * bytespp;
		offset += fb->vi.xres * fb->vi.yoffset * bytespp;
		bits = fb->bits + offset / sizeof(*fb->bits);
	}
	return bits;
}

void fb_update(struct FB *fb)
{
	if (fb) {
		fb->vi.yoffset = 1;
		ioctl(fb->fd, FBIOPUT_VSCREENINFO, &fb->vi);
		fb->vi.yoffset = 0;
		ioctl(fb->fd, FBIOPUT_VSCREENINFO, &fb->vi);
	}
}

static int fb_open(struct FB *fb)
{
	if (NULL == fb) {
		return -1;
	}

	fb->fd = open("/dev/graphics/fb0", O_RDONLY);
	if (fb->fd < 0) {
		printf("open(\"/dev/graphics/fb0\") failed!\n");
		LOGI("---open(\"/dev/graphics/fb0\") failed!---");
		return -1;
	}

	if (ioctl(fb->fd, FBIOGET_FSCREENINFO, &fb->fi) < 0) {
		printf("FBIOGET_FSCREENINFO failed!\n");
		LOGI("---FBIOGET_FSCREENINFO failed!---");
		goto fail;
	}
	if (ioctl(fb->fd, FBIOGET_VSCREENINFO, &fb->vi) < 0) {
		printf("FBIOGET_VSCREENINFO failed!\n");
		LOGI("---FBIOGET_VSCREENINFO failed!---");
		goto fail;
	}
	len=fb_virtual_size(fb);
	fb->bits = mmap(0, fb_virtual_size(fb), PROT_READ, MAP_SHARED, fb->fd, 0);

	//闂備礁鎼悮顐﹀磿閸愯鑰块柛妤佹晜amebuffer濠电偠鎻徊鐣岀矓閺夋嚚鐟邦潨閿熻棄顕ｆ禒瀣亗閹艰揪绲块、锟�
	LOGI("---framebuffer鍍忕礌%d---", fb->vi.bits_per_pixel);

	if (fb->bits == MAP_FAILED) {
		printf("mmap() failed!\n");
		LOGI("---mmap()濠电姰鍨洪崕鑲╁垝閸撗勫枂闁挎洖鍊归弲顒勬煥閻曞倹瀚�---");
		goto fail;
	}

	return 0;

	fail:
	LOGI("---fb_open()濠电姰鍨洪崕鑲╁垝閸撗勫枂闁挎洖鍊归弲顒勬煥閻曞倹瀚�---");
	close(fb->fd);
	return -1;
}

static void fb_close(struct FB *fb)
{
	if (fb) {
		munmap(fb->bits, fb_virtual_size(fb));
		close(fb->fd);
	}
}

static struct FB g_fb;
struct FB * fb_create(void)
{
	memset(&g_fb, 0, sizeof(struct FB));
	if (fb_open(&g_fb)) {
		return NULL;
	}
	return &g_fb;
}

void fb_destory(struct FB *fb)
{
	fb_close(fb);
}
int save_bmp(const char * path, int w, int h, void * pdata, int bpp) {
	int success = 0;
	//FILE * hfile = NULL;
	FILE *cach_file=NULL;
	do {
		if (path == NULL || w <= 0 || h <= 0 || pdata == NULL) {
			printf("if (path == NULL || w <= 0 || h <= 0 || pdata == NULL)\n");
			LOGI("---璺緞涓虹┖---");
			if(path == NULL){
				LOGI("---path = NULL---");
			}
			if(pdata == NULL){
				LOGI("---pdata = NULL---");
			}
			break;
		}

		remove(path);
//		hfile = fopen(path, "wb");
//		if (hfile == NULL) {
//			printf("open(%s) failed!\n", path);
//			LOGI("---鎵撳紑鏂囦欢澶辫触锟�---");
//			break;
//		}
//		char *cach="/sdcard/mypic.raw";
		cach_file=fopen(path, "wb");
		if(cach_file==NULL){
			LOGI("鎵撳紑缂撳瓨鏂囦欢澶辫触");
			break;
		}
		switch (bpp) {
//		case 16:
//			success = save_bmp_rgb565(hfile, w, h, pdata);
//			break;
//		case 24:
//			success = save_bmp_rgb888(hfile, w, h, pdata);
//			break;
		case 32:
			fwrite(pdata, 1, w * h * 4, cach_file);
			//success = save_bmp_rgb8888(hfile, w, h, pdata);
			break;
		default:
			printf("error: not support format!\n");
			LOGI("---娑撳秵鏁幐浣烘畱閺嶇厧绱￠敍锟�---");
			success = 0;
			break;
		}
	} while (0);

//	if (hfile != NULL)
//		fclose(hfile);
	fseek(cach_file,0,2);
	int n=ftell(cach_file);
	if (cach_file != NULL)
			fclose(cach_file);
	LOGI("cach鈥斺�攆ile=%d",n);
	return success;
}

JNIEXPORT jint JNICALL Java_com_shareScreen_CODE_encode
(JNIEnv * env, jobject thiz, jint width, jint height){
	LOGI("---start to cut the screen---");
	fb_create();
	//int len= width*height*g_fb.vi.bits_per_pixel/8;
	LOGI("len=%d",len);
	if(g_fb.bits==NULL){
		LOGI("NULL POINTER");
		return -1;
	}
	//save_bmp("/sdcard/mypic.raw", width, height, g_fb.bits, g_fb.vi.bits_per_pixel);
	uint8_t *p;
	p=malloc(len);
	LOGI("1111");
	memcpy(p, g_fb.bits,len);

	LOGI("---start to rgb to yuv---");
	int i=rgb_to_yuv(width,height,p);
	//int i= rgb_to_yuv(int w,int h);
	if(i==-1){
		LOGI("rgb to yuv failed!");
		//free(p);
		//p=NULL;
		return -1;
	}
	i=ffmpeg_encode(height,width);
	free(p);
	p=NULL;
	fb_destory(&g_fb);
	return i;
}
int rgb_to_yuv(int w,int h,uint8_t *p){
//int rgb_to_yuv(int w,int h){
//	LOGI("open mypic.raw ");
//	FILE *src_file = fopen("/sdcard/mypic.raw", "rb");
//	if(src_file==NULL){
//		LOGI("open mypic.raw failed!");
//		return -1;
//	}
	int src_w = w, src_h = h;
	//enum AVPixelFormat src_pixfmt = AV_PIX_FMT_RGB24;
	enum AVPixelFormat src_pixfmt = AV_PIX_FMT_RGBA;
	int src_bpp = av_get_bits_per_pixel(av_pix_fmt_desc_get(src_pixfmt));
	LOGI("src_bpp%d",src_bpp);
	FILE *dst_file = fopen("/sdcard/mypic.yuv", "wb");
	if(dst_file==NULL){
		LOGI("open mypic.yuv failed!");
		return -1;
	}
	int dst_w = w, dst_h = h;
	enum AVPixelFormat dst_pixfmt = AV_PIX_FMT_YUV420P;
	int dst_bpp = av_get_bits_per_pixel(av_pix_fmt_desc_get(dst_pixfmt));

	//Structures
	uint8_t *src_data[4];
	int src_linesize[4];

	uint8_t *dst_data[4];
	int dst_linesize[4];

	int rescale_method = SWS_BICUBIC;
	struct SwsContext *img_convert_ctx;
	//uint8_t *temp_buffer = (uint8_t *) malloc(src_w * src_h * src_bpp / 8);
	uint8_t *temp_buffer=p;
	int frame_idx = 0;
	int ret = 0;
	ret = av_image_alloc(src_data, src_linesize, src_w, src_h, src_pixfmt, 1);
	if (ret < 0) {
		LOGI("Could not allocate source image\n");
		return -1;
	}
	ret = av_image_alloc(dst_data, dst_linesize, dst_w, dst_h, dst_pixfmt, 1);
	if (ret < 0) {
		LOGI("Could not allocate destination image\n");
		return -1;
	}
	//-----------------------------
	//Init Method 1

	img_convert_ctx = sws_alloc_context();
	//Show AVOption

	av_opt_set_int(img_convert_ctx, "sws_flags", SWS_BICUBIC | SWS_PRINT_INFO,
			NULL);
	av_opt_set_int(img_convert_ctx, "srcw", src_w, NULL);
	av_opt_set_int(img_convert_ctx, "srch", src_h, NULL);
	av_opt_set_int(img_convert_ctx, "src_format", src_pixfmt, NULL);
	av_opt_set_int(img_convert_ctx, "src_range", 1, NULL);
	av_opt_set_int(img_convert_ctx, "dstw", dst_w, NULL);
	av_opt_set_int(img_convert_ctx, "dsth", dst_h, NULL);
	av_opt_set_int(img_convert_ctx, "dst_format", dst_pixfmt, NULL);
	av_opt_set_int(img_convert_ctx, "dst_range", 1, NULL);
	sws_init_context(img_convert_ctx, NULL, NULL);
	ret=sws_setColorspaceDetails(img_convert_ctx,sws_getCoefficients(SWS_CS_ITU601),0,
			sws_getCoefficients(SWS_CS_ITU709),0,
			0, 1 << 16, 1 << 16);
	if (ret==-1) {
		printf( "Colorspace not support.\n");
		return -1;
	}
	int length=0;

	/*while (1)*/{
//		LOGI("read mypic.raw");
//		if (fread(temp_buffer, 1, src_w * src_h * src_bpp / 8, src_file)
//				!= src_w * src_h * src_bpp / 8) {
//			break;
//		}
		switch (src_pixfmt) {
		case AV_PIX_FMT_GRAY8: {
			memcpy(src_data[0], temp_buffer, src_w * src_h);
			break;
		}
		case AV_PIX_FMT_YUV420P: {
			memcpy(src_data[0], temp_buffer, src_w * src_h);                 //Y
			memcpy(src_data[1], temp_buffer + src_w * src_h, src_w * src_h / 4); //U
			memcpy(src_data[2], temp_buffer + src_w * src_h * 5 / 4,
					src_w * src_h / 4);  //V
			break;
		}
		case AV_PIX_FMT_YUV422P: {
			memcpy(src_data[0], temp_buffer, src_w * src_h);                 //Y
			memcpy(src_data[1], temp_buffer + src_w * src_h, src_w * src_h / 2); //U
			memcpy(src_data[2], temp_buffer + src_w * src_h * 3 / 2,
					src_w * src_h / 2);  //V
			break;
		}
		case AV_PIX_FMT_YUV444P: {
			memcpy(src_data[0], temp_buffer, src_w * src_h);                 //Y
			memcpy(src_data[1], temp_buffer + src_w * src_h, src_w * src_h); //U
			memcpy(src_data[2], temp_buffer + src_w * src_h * 2, src_w * src_h); //V
			break;
		}
		case AV_PIX_FMT_YUYV422: {
			memcpy(src_data[0], temp_buffer, src_w * src_h * 2);        //Packed
			break;
		}
		case AV_PIX_FMT_RGB24: {

			memcpy(src_data[0], temp_buffer, src_w * src_h * 3);        //Packed
			break;
		}
		case AV_PIX_FMT_RGBA: {
			LOGI("ABC");
			memcpy(src_data[0], temp_buffer, len);        //Packed
			//memcpy(src_data[0], p, src_w * src_h * 4);
			break;
		}
		default: {
			printf("Not Support Input Pixel Format.\n");
			break;
		}
		}
		sws_scale(img_convert_ctx, src_data, src_linesize, 0, src_h, dst_data,
				dst_linesize);
		printf("Finish process frame %5d\n", frame_idx);
		frame_idx++;

		switch (dst_pixfmt) {
		case AV_PIX_FMT_GRAY8: {
			fwrite(dst_data[0], 1, dst_w * dst_h, dst_file);
			break;
		}
		case AV_PIX_FMT_YUV420P: {
			LOGI("wirte yuv file");
			fwrite(dst_data[0], 1, dst_w * dst_h, dst_file);                 //Y
			fwrite(dst_data[1], 1, dst_w * dst_h / 4, dst_file);             //U
			fwrite(dst_data[2], 1, dst_w * dst_h / 4, dst_file);             //V
			/*uint8_t *picture_buf;
			picture_buf = (uint8_t *)av_malloc(dst_w * dst_h*3/2);
			memcpy(picture_buf,dst_data[0],dst_w * dst_h);
			memcpy(picture_buf+ dst_w * dst_h,dst_data[1],dst_w * dst_h/4);
			memcpy(picture_buf+ dst_w * dst_h*5/4,dst_data[2],dst_w * dst_h/4);
			ffmpeg_encode(dst_w,dst_h,picture_buf);
			free(picture_buf);*/
			break;
		}
		case AV_PIX_FMT_YUV422P: {
			fwrite(dst_data[0], 1, dst_w * dst_h, dst_file);				//Y
			fwrite(dst_data[1], 1, dst_w * dst_h / 2, dst_file);			//U
			fwrite(dst_data[2], 1, dst_w * dst_h / 2, dst_file);			//V
			break;
		}
		case AV_PIX_FMT_YUV444P: {
			fwrite(dst_data[0], 1, dst_w * dst_h, dst_file);                 //Y
			fwrite(dst_data[1], 1, dst_w * dst_h, dst_file);                 //U
			fwrite(dst_data[2], 1, dst_w * dst_h, dst_file);                 //V
			break;
		}
		case AV_PIX_FMT_YUYV422: {
			fwrite(dst_data[0], 1, dst_w * dst_h * 2, dst_file);        //Packed
			break;
		}
		case AV_PIX_FMT_RGB24: {
			fwrite(dst_data[0], 1, dst_w * dst_h * 3, dst_file);        //Packed
			break;
		}
		case AV_PIX_FMT_RGBA: {
			fwrite(dst_data[0], 1, dst_w * dst_h * 4, dst_file);
			break;
		}
		default: {
			printf("Not Support Output Pixel Format.\n");
			break;
		}
		}
	}
	sws_freeContext(img_convert_ctx);
	//free(temp_buffer);
	fclose(dst_file);
	LOGI("close yuv file");
	av_freep(&src_data[0]);
	av_freep(&dst_data[0]);
	return 0;
}
int flush_encoder(AVFormatContext *fmt_ctx,unsigned int stream_index)
{
	int ret;
	int got_frame;
	AVPacket enc_pkt;
	if (!(fmt_ctx->streams[stream_index]->codec->codec->capabilities &
			CODEC_CAP_DELAY))
		return 0;
	while (1) {
		LOGI("Flushing stream #%u encoder\n", stream_index);
		//ret = encode_write_frame(NULL, stream_index, &got_frame);
		enc_pkt.data = NULL;
		enc_pkt.size = 0;
		av_init_packet(&enc_pkt);
		ret = avcodec_encode_video2 (fmt_ctx->streams[stream_index]->codec, &enc_pkt,
				NULL, &got_frame);
		av_frame_free(NULL);
		if (ret < 0)
			break;
		if (!got_frame)
		{ret=0;break;}
		LOGI("缂傚倸鍊归悧婊堟偉濠婂牆绠ｉ柟閭﹀墮椤拷1闁汇埄鍨庡鍥╋紲\n");
		ret = av_write_frame(fmt_ctx, &enc_pkt);
		if (ret < 0)
			break;
	}
	return ret;
}

//int ffmpeg_encode(int h,int w,uint8_t *p[4])
int ffmpeg_encode(int h,int w)
{
	AVFormatContext* pFormatCtx;
	AVOutputFormat* fmt;
	AVStream* video_st;
	AVCodecContext* pCodecCtx;
	AVCodec* pCodec;

	uint8_t* picture_buf;
	AVFrame* picture;
	int size;
	LOGI("start encoder");
	LOGI("open yuv file");
	FILE *in_file = fopen("/sdcard/mypic.yuv", "rb");	//闁荤喐鐟ュΛ婵嬨�傜粋鐢嶸濠电姍鍕闁哄鍟粋鎺楁晸閿燂拷
	if(in_file==NULL)
	{
		LOGI("闂佺懓鐏氶幐鍝ユ閹达箑妫橀柛銉檮椤愯棄顭块幆鎵翱閻熸瑱绠撻弫宥夋晸閿燂拷");
		return -1;
	}
	int in_w=w,in_h=h;//闁诲酣锟芥稓鐭欓柣婵撴嫹
	int framenum=50;
	const char* out_file = "/sdcard/mypic.h264";					//闁哄鐗婇幐鎼佸吹椤撱垹妫橀柛銉檮椤愪粙鎮规笟顖氱仩缂佽鎷�

	av_register_all();
	//闂佸搫鍊介～澶屾兜閿燂拷1.缂傚倷绀佺�氼剟骞冩惔銏″閻犳亽鍔嶉弳蹇涙煕閹寸姴小闂佸弶绮撳畷娆撴嚍閵夛附顔�
	pFormatCtx = avformat_alloc_context();
	//闂佺粯纰嶇划宥夋偋缁嬫鍤曢柨鐕傛嫹
	fmt = av_guess_format(NULL, out_file, NULL);
	pFormatCtx->oformat = fmt;

	//闂佸搫鍊介～澶屾兜閿燂拷2.闂佸搫娲﹀娆愭叏閻愮儤鍤婃い蹇撳琚熼梺鍛婄墬閻楁梻绮╃�涙顩查柨鐕傛嫹
	//avformat_alloc_output_context2(&pFormatCtx, NULL, NULL, out_file);
	//fmt = pFormatCtx->oformat;


	//濠电偛顦崝宥夊礈閻楀牊缍囬柟鎯у暱濮ｅ鎮规笟顖氱仩缂佽鎷�
	if (avio_open(&pFormatCtx->pb,out_file, AVIO_FLAG_READ_WRITE) < 0)
	{
		LOGI("闁哄鐗婇幐鎼佸吹椤撱垹妫橀柛銉檮椤愪粙鏌熼崹顐ｅ碍缂佽鲸鍨跺鍕綇椤愩儛锟�");
		return -1;
	}

	video_st = avformat_new_stream(pFormatCtx, 0);
	if (video_st==NULL)
	{
		return -1;
	}
	pCodecCtx = video_st->codec;
	pCodecCtx->codec_id = fmt->video_codec;
	pCodecCtx->codec_type = AVMEDIA_TYPE_VIDEO;
	pCodecCtx->pix_fmt = PIX_FMT_YUV420P;
	//pCodecCtx->pix_fmt = PIX_FMT_RGBA;
	pCodecCtx->width = in_w;
	pCodecCtx->height = in_h;
	pCodecCtx->time_base.num = 1;
	pCodecCtx->time_base.den = 25;
	pCodecCtx->bit_rate = 1000000;
	pCodecCtx->gop_size=250;
	//H264
	//pCodecCtx->me_range = 16;
	//pCodecCtx->max_qdiff = 4;
	pCodecCtx->qmin = 10;
	pCodecCtx->qmax = 51;
	//pCodecCtx->qcompress = 0.6;
	//闁哄鐗婇幐鎼佸吹椤撱垹鍐�闁绘挸娴风涵锟芥繛锝呮礌閸撴繃瀵奸敓锟�
	av_dump_format(pFormatCtx, 0, out_file, 1);
	//pCodec = avcodec_find_encoder(pCodecCtx->codec_id);
	pCodec=avcodec_find_encoder(AV_CODEC_ID_H264);
	if (!pCodec)
	{
		LOGI("濠电偛澶囬崜婵嗭耿娓氾拷楠炲秹骞嗚閻撳倿鏌涘顒傚ⅵ闁跨喕妫勯崐璇测枔閹寸姷纾介柡宥庡亞閸ㄦ娊鏌涢敐搴ｅ帨缂佽鲸濡皀");
		return -1;
	}
	if (avcodec_open2(pCodecCtx, pCodec,NULL) < 0)
	{
		LOGI("缂傚倸鍊归悧婊堟偉濠婂牆闂柕濠忕畱閳數锟芥鍣幏宄邦熆閹壆绨块悷娆欑畵閺佸秵绋婂锟�");
		return -1;
	}
	picture = avcodec_alloc_frame();
	size = avpicture_get_size(pCodecCtx->pix_fmt, pCodecCtx->width, pCodecCtx->height);
	picture_buf = (uint8_t *)av_malloc(size);
	avpicture_fill((AVPicture *)picture, picture_buf, pCodecCtx->pix_fmt, pCodecCtx->width, pCodecCtx->height);

	//闂佸憡鍔栭悷锕傚几閸愨晝顩烽悹鐑樹航娴狅拷
	avformat_write_header(pFormatCtx,NULL);

	AVPacket pkt;
	int y_size = pCodecCtx->width * pCodecCtx->height;
	av_new_packet(&pkt,y_size*3);
	int i=0;
	for (i=0; i<framenum; i++){
		//闁荤姴娲╅褔宕ｉ崫鐞擵
		LOGI("read yuv file");
		if (fread(picture_buf, 1, y_size*3/2, in_file) < 0)
		{
			LOGI("闂佸搫鍊稿ú锝呪枎閵忋垺瀚氶悹鍥ㄥ絻缁插潡姊洪幐搴ｆ噯妞ゆ洦妲�n");
			return -1;
		}else if(feof(in_file)){
			break;
		}
		picture->data[0] = picture_buf;  // 婵炲瓨绮犻崰鏍拷鍦埓
		picture->data[1] = picture_buf+ y_size;  // U
		picture->data[2] = picture_buf+ y_size*5/4; // V

		picture->pts=i;
	}
	int got_picture=0;
	//缂傚倸鍊归悧婊堟偉閿燂拷
	int ret = avcodec_encode_video2(pCodecCtx, &pkt,picture, &got_picture);
	if(ret < 0)
	{
		LOGI("缂傚倸鍊归悧婊堟偉濠婂牊鐓ユ繛鍡樺俯閸ゆ牠鏌ㄥ☉妤冾啈n");
		return -1;
	}
	if (got_picture==1)
	{
		LOGI("缂傚倸鍊归悧婊堟偉濠婂牆绠ｉ柟閭﹀墮椤拷1闁汇埄鍨庡鍥╋紲\n");
		pkt.stream_index = video_st->index;
		ret = av_write_frame(pFormatCtx, &pkt);
		av_free_packet(&pkt);
	}
	//}
	//Flush Encoder
	int ret_f = flush_encoder(pFormatCtx,0);
	if (ret_f < 0) {
		LOGI("Flushing encoder failed\n");
		return -1;
	}

	//闂佸憡鍔栭悷锕傚几閸愨晝顩烽悹楦挎閸燂拷
	av_write_trailer(pFormatCtx);

	//濠电偞鎸搁幊鎰板箖閿燂拷
	if (video_st)
	{
		avcodec_close(video_st->codec);
		av_free(picture);
		av_free(picture_buf);
	}
	avio_close(pFormatCtx->pb);
	avformat_free_context(pFormatCtx);

	fclose(in_file);

	return 0;
}
#endif//#ifndef WIN32
