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

int yuv_to_rgb(int w,int h,AVFrame	*pFrame);
int save_bmp1(int w,int h,uint8_t *p);
int ffmepg_decorder(int w,int h);

typedef struct bmp_header {
	short twobyte; //婵炴垶鎸堕崐鎾绘煂濠婂拑鎷峰☉娆樻畽濠碘槅鍙冮弫宥呯暆閿熶粙寮妶澶婄骇闁靛鍊楃粻浠嬫偣閸ワ附顦风紒妤�鎳樺Λ鍐閳ュ磭浜ｉ梺鍛婄♁椤洭骞撻敐澶婄闁瑰鍋涚粭鎾绘煕閹烘搩娈ｇ紒杈ㄧ箖濞艰鈻庢惔銈傛嫛婵炴垶鎼╂禍婊堟偤瑜忕划顓㈡晜鐠恒劎鎲归梺鐓庣枃濡嫰宕幘璇茬闁绘鐗婇悗顔济归悩铏瀯闁肩鎷�
	//14B
	char bfType[2]; //!闂佸搫鍊稿ú锝呪枎閵忋倖鍎嶉柛鏇ㄥ弨椤箓鏌涢…鎺撳,闁荤姴娲㈤崕閬嶆晸閻ｅ苯甯剁紒浼欐嫹闂傚倸娴勯幏鐑芥煛閸曡埖瀚�0x4D42闂佹寧绋戞總鏃傚姬閸愵亙鐒婇弶鍫涘妽绗戦柣搴㈢♁椤ㄥ牓顢栭敓锟�'BM'
	unsigned int bfSize; //!闁荤姴娲ら悺銊ノｉ幋锕�妫橀柛銉檮椤愪粙鏌ｉ妸銉ヮ仼闁靛洤娲ㄦ禍姝岀疀閵壯咁槷闂佺儵鍋撻崝宀勬偤瑜旈幊鐐哄磼濞嗘帞顦伴梺鍛婎殕濞叉垹绱為敓锟�
	unsigned int bfReserved1; //婵烇絽娲︾换鍕汲閿熶粙鏌ㄥ☉妯垮缂佷紮鎷锋俊鐐�栧Σ鎺楊敊閺囩姷纾炬い鏃�妲掔粈锟�0
	unsigned int bfOffBits; //!闁荤姴娲ら悺銊ノｉ幋鐐殿浄閹兼番鍨洪悗顔济归悩鐑樼【闁靛洦鏌ㄩ锝夋晸閼恒儲鍙忛悗锝庝簻閻撳倿鎮楅崷顓炰槐婵＄虎鍨堕幆鍐礋椤愶絿顩柣鐘靛亹閸撴繈寮抽悢鐓庣妞ゆ棁宕甸娲⒒閸屾凹鍤熸繛鍫熷灩閿熻姤绋掗〃澶嬩繆椤撱垺鍎嶉柛鏇ㄥ亗閻掑﹦绱掓径濠庣吋闁革絾妞介弫宥呯暆閸愵亞顔愰梻浣瑰絻婵傛梻鎷归敓锟�14B+sizeof(BMPINFO)
} BMPHEADER;

typedef struct bmp_info {
	//40B
	unsigned int biSize; //!BMPINFO缂傚倷鐒﹂幐濠氭倵椤栫偛绠ラ柨鐔剁矙濡線鏁撻悾灞惧暫濞达絿鍎ら悾閬嶆倵濞戞瑯娈曢柡鍡嫹
	int biWidth; //!闂佹悶鍎插畷姗�鎸呴崟顖涘剭闁告洦鍋掗崯宥夊箹鏉堟崘顓虹紒杈ㄧ箖缁傛帡濡烽妸銊︽緰缂備浇浜慨宕囨嫻閻旂厧纭�闁哄洦姘ㄧ粔锟�
	int biHeight; //!闂佹悶鍎插畷姗�鎸呴崟顖涘剭闁告洦鍋掗崯宥夊箹鏉堟崘顓虹紒杈ㄧ箖缁傛帡濡烽妸銊︽緰缂備浇浜慨宕囨嫻閻旂厧纭�闁哄洦姘ㄧ粔锟�,婵犵锟藉啿锟藉綊鎮樻径灞惧珰闁靛鍎婚幏鐑藉礂閸忓す锔芥叏濠垫挾绋婚柡鍡欏枛閺佸秴鐣濋崘鎯ф闂佸搫瀚绋棵瑰锟藉畷鎾圭疀閺冿拷绗戦梺绋匡攻鐢帡骞冨鍫熷剭闁告洖澧庣粈澶娾攽閳ュ啿锟藉綊鎮樻径灞惧珰闁靛鍎婚幏鐑藉礂閸忓す锕傛偣閹邦喖鏋戦柡鍡欏枛閺佸秶浠﹂挊澶屼户闂佸搫瀚烽崹閬嶎敆濠婂牆瑙﹂柟瀵稿У閻ｏ拷
	unsigned short biPlanes; //!闂佺儵鏅╅崰妤呮偉閿濆洦濯奸柟顖嗗本校闁荤姴娲ら悺銊ノｉ幋鐐村鐎广儱顦板銊╂煛娴ｇ绨荤紒杈ㄧ箞瀹曟鎹勯悮瀛樺闁绘挸娴峰▓娲煙椤掑倻甯涙俊鎻掓憸閹噣顢曢妷顔兼櫗婵炴垶鎼幏锟�1
	unsigned short biBitCount; //!濠殿噯绲惧缁樼珶閹烘鏋侀柨鐕傛嫹/闁荤姷鍎戠槐鏇犵矆瀹�鍕櫖閻忕偠妫勫鎾绘煕婵犲洦锛熼悹鎰舵嫹1闂侀潧妫撮幏锟�4闂侀潧妫撮幏锟�8闂侀潧妫撮幏锟�16闂侀潧妫撮幏锟�24闂侀潧妫旈悞锕傚垂閿燂拷32
	unsigned int biCompression; //闁荤姴娲ら悺銊ノｉ幋锕�鐐婇柟瑙勫姌閺変粙鏌℃担鍝勵暭鐎规挷绶氬畷銏拷锝庡墰缁辨岸鏌ｉ妸銉ヮ伀閻炴凹鍋婂畷褰掓晸閿燂拷
#define BI_RGB        0L    //濠电偛澶囬崜婵嗭耿娓氾拷瀹曘垻锟斤綆鍓涚槐锟�
#define BI_RLE8       1L    //濠殿噯绲界换瀣煂濠婂懏瀵柨娑樺椤︼拷8濠殿噯绲惧缁樼珶閹烘鍎嶉柛鎴狀暰E闂佸憡锚椤戝洨绱撴径宀�纾介柡宥庡亞閸ㄦ娊鏌ㄥ☉妯垮閻㈩垰娲ㄧ槐鎾诲煛閸屾粌顥曢悗娈垮枛缁绘帡寮敓锟�2闁诲孩绋掗〃澶嬩繆椤撶姷纾奸柛鏇ㄥ墮閻忓洭鏌ㄥ☉妯煎ⅵ闁革絽鎲″鍕吋閸絾婢栫紓浣戒含婵箖顢橀幖浣告瀬闁哄瀵х�氭彃螞閻楀牏鐭庡鐟帮功濡叉劙濮�閻樼數鈹涢梺鎸庣☉椤э拷缂佹唻鎷�
#define BI_RLE4       2L    //濠殿噯绲界换瀣煂濠婂懏瀵柨娑樺椤︼拷4濠殿噯绲惧缁樼珶閹烘鍎嶉柛鎴狀暰E闂佸憡锚椤戝洨绱撴径宀�纾介柡宥庡亞閸ㄦ娊鏌ㄥ☉妯垮閻㈩垰娲ㄧ槐鎾诲煛閸屾粌顥曢悗娈垮枛缁绘帡寮敓锟�2闁诲孩绋掗〃澶嬩繆椤撶姷纾奸柛鏇ㄥ墮閻忥拷
#define BI_BITFIELDS  3L    //濠殿噯绲界换瀣煂濠婂懏瀵柨娑樺椤﹂亶鏌ｉ妸銉ヮ仾闁伙富鍣ｉ幃褔宕烽鐔告闂佸湱顭堝ú銈夋偩妤ｅ啯鍎嶉柛鏇ㄥ墮鐠愮喖鏌ｉ鑽ゎ槮闁哥姴鎳愰敓鍊燁潐閸濆酣鏁撻弬銈嗗
	unsigned int biSizeImage; //闂佹悶鍎插畷姗�鎸呴崟顖涘剭闁告洦鍋佹禍锝夋倶韫囨捇鐛滅紒杈ㄧ箖缁傛帡濡烽妷褎鎲婚梺鐓庢惈閸婇鎷归悢鐓庣闁哄洦姘ㄧ粔鎾煏閸℃锟藉摜绱炵�ｎ喗鍋ㄩ挊鐩淿RGB闂佸搫绉堕崢褏妲愰敓鐘茬睄閻犱礁婀辩粈澶愭煕濞嗘ê鐏ユい鏃�娲滅槐鏃堫敊閺勫繒顦�0
	int biXPelsPerMeter; //濠殿喗蓱濞兼瑩鏌﹂埡鍛闁糕剝宀搁崫娲煟濠婂啯绁紒杈ㄧ箞閹粙濡搁妸銊︽緰缂備焦鍞婚幏锟�/缂備緡鍋勯悿鍥Υ閸愵亞鐭嗛柨鐕傛嫹
	int biYPelsPerMeter; //闂佹悶鍔岄崐璇裁洪崸妤�绀嗛柛鈩冨哺閸濇椽鏌ｅ鍐╃カ缂佽鲸绻堥幃浠嬪Ω閵娿劍婢栫紓浣瑰敾閹凤拷/缂備緡鍋勯悿鍥Υ閸愵亞鐭嗛柨鐕傛嫹
	unsigned int biClrUsed; //婵炶揪绲界粔鏉懨瑰Ο纭锋嫹閸︻厼浠辨俊缁㈠灡閹峰懐鎹勯妸锔芥闂佹眹鍔岀�氼剟宕撻悽鍛婂殞闁艰壈缈伴敓钘夊暞缁嬪顢橀悩宕囨殸婵☆偆澧楃划蹇旂珶婵犲嫭顫曢柕蹇曞Х缁屽潡鏌℃担绋跨盎缂佽鲸鐟ч幏瀣矗閵夈劎顦�0闂佹眹鍔岀�氼垶鎯佹禒瀣櫖閻忕偠妫勯悘鐔兼偣閸パ呮憼婵″弶鍨堕幏鍛崉閵婏附娈㈤梺鍦暜閹风兘鏌￠崼婵愭Ъ闁汇劎鍠栭幊婵嬪矗婢跺鍑℃俊鐐�楀▍銏㈡閿燂拷
	unsigned int biClrImportant; //闁诲海鏁搁幊鎾趁瑰Ο鍏煎闁斥晛鍟埢鏃傜磼閿熶粙鎮滃Ο鑽ょ暢闂備焦褰冪粔鐑姐�呴敃浣典汗闁告鍋為幆娆撴煟閵娿儱顏い銈勭窔閹虫繈鎳犻锟介崑宥囷拷娈垮枟濞叉粌鈻撻幋锕�鏋侀柟娈垮枟缁愭鏌ㄥ☉妯垮妞も敪鍥у嚑婵犲﹤瀚瑧0闂佹寧绋戦惌渚�濡撮崘顏嗙焼婵炲樊浜滈崢鎾⒑閹绘帞啸妞も晪绠撴俊鎾晸閿燂拷
} BMPINFO;

typedef struct tagRGBQUAD {
	unsigned char rgbBlue;
	unsigned char rgbGreen;
	unsigned char rgbRed;
	unsigned char rgbReserved;
} RGBQUAD;

typedef struct tagBITMAPINFO {
	BMPINFO bmiHeader;
	//RGBQUAD    bmiColors[1];
	unsigned int rgb[3];
} BITMAPINFO;

static int get_rgb888_header(int w, int h, BMPHEADER * head, BMPINFO * info) {
	int size = 0;
	if (head && info) {
		size = w * h * 3;
		memset(head, 0, sizeof(*head));
		memset(info, 0, sizeof(*info));
		head->bfType[0] = 'B';
		head->bfType[1] = 'M';
		head->bfOffBits = 14 + sizeof(*info);
		head->bfSize = head->bfOffBits + size;
		head->bfSize = (head->bfSize + 3) & ~3; //windows闁荤喐娲戦悞锕傛儑娴兼潙妫橀柛銉檮椤愯棄顭块崼鍡楀暟濮ｅ牓鐓崶褎鍤囬柕鍡楃箻瀵即鏁撻敓锟�4闂佹眹鍔岀�氼剟鏁撻挊澶屝ｉ柡鍡嫹
		size = head->bfSize - head->bfOffBits;

		info->biSize = sizeof(BMPINFO);
		info->biWidth = w;
		info->biHeight = -h;
		info->biPlanes = 1;
		info->biBitCount = 24;
		info->biCompression = BI_RGB;
		info->biSizeImage = size;

		printf("rgb888:%dbit,%d*%d,%d\n", info->biBitCount, w, h, head->bfSize);
	}
	return size;
}

static int get_rgb8888_header(int w, int h, BMPHEADER * head, BMPINFO * info) {
	int size = 0;
	if (head && info) {
		size = w * h * 4;
		LOGI("W*H*4=%d",size);
		memset(head, 0, sizeof(*head));
		memset(info, 0, sizeof(*info));
		head->bfType[0] = 'B';
		head->bfType[1] = 'M';
		head->bfOffBits = 14 + sizeof(*info);
		head->bfSize = head->bfOffBits + size;
		head->bfSize = (head->bfSize + 3) & ~3; //windows闁荤喐娲戦悞锕傛儑娴兼潙妫橀柛銉檮椤愯棄顭块崼鍡楀暟濮ｅ牓鐓崶褎鍤囬柕鍡楃箻瀵即鏁撻敓锟�4闂佹眹鍔岀�氼剟鏁撻挊澶屝ｉ柡鍡嫹
		size = head->bfSize - head->bfOffBits;
		info->biSize = sizeof(BMPINFO);
		info->biWidth = w;
		info->biHeight = -h;
		info->biPlanes = 1;
		info->biBitCount = 32;
		info->biCompression = BI_RGB;
		info->biSizeImage = size;

		printf("rgb8888:%dbit,%d*%d,%d\n", info->biBitCount, w, h, head->bfSize);
	}
	return size;
}

static int get_rgb565_header(int w, int h, BMPHEADER * head, BITMAPINFO * info) {
	int size = 0;
	if (head && info) {
		size = w * h * 2;
		memset(head, 0, sizeof(*head));
		memset(info, 0, sizeof(*info));
		head->bfType[0] = 'B';
		head->bfType[1] = 'M';
		head->bfOffBits = 14 + sizeof(*info);
		head->bfSize = head->bfOffBits + size;
		head->bfSize = (head->bfSize + 3) & ~3;
		size = head->bfSize - head->bfOffBits;

		info->bmiHeader.biSize = sizeof(info->bmiHeader);
		info->bmiHeader.biWidth = w;
		info->bmiHeader.biHeight = -h;
		info->bmiHeader.biPlanes = 1;
		info->bmiHeader.biBitCount = 16;
		info->bmiHeader.biCompression = BI_BITFIELDS;
		info->bmiHeader.biSizeImage = size;

		//闂佸憡鐟禍婵嗭耿閿燂拷16婵炶揪绲界粔鍨櫠閻樼粯顥嗛柨鐔烘櫕閹茬増鎷呴悾灞绢啀闁荤姴娲﹀ú妯侯熆濮楋拷閺佸秹鏁撻敓锟�24婵炴垶鎼幏锟�32婵炶揪绲界粔宕囩箔婢舵劖顥嗛柨鐔烘櫕閹蹭即鏁撻敓锟�
		info->rgb[0] = 0xF800;
		info->rgb[1] = 0x07E0;
		info->rgb[2] = 0x001F;

		printf("rgb565:%dbit,%d*%d,%d\n", info->bmiHeader.biBitCount, w, h,
				head->bfSize);
	}
	return size;
}
static int save_bmp_rgb565(FILE * hfile, int w, int h, void * pdata) {
	int success = 0;
	int size = 0;
	BMPHEADER head;
	BITMAPINFO info;

	size = get_rgb565_header(w, h, &head, &info);
	if (size > 0) {
		fwrite(head.bfType, 1, 14, hfile);//闁哄鏅滈悷鈺呭闯閻戣棄绠┑鍌滅┅ad闂佽桨鑳舵晶妤�鐣垫担铏圭＜闁规儳顕敓绛嬪灦閺屽苯鐣濋崟顒併�冮梺姹囧妼鐎氼厽鏅跺澶婂珘濠㈣泛顑嗗▓鍫曟煙鐠団�虫珝闁稿繑锕㈠畷妯衡枎韫囨挸姹查梺鍝勫�稿ú锝呪枎閵忋倖鐓傞悘鐐跺亹閸熷﹪鏌ㄥ☉妯垮婵炲弶绮嶇粙澶愭倻濡亶锕傛煕閹邦厾鎳冮柛娆欐嫹14B
		fwrite(&info, 1, sizeof(info), hfile);
		fwrite(pdata, 1, size, hfile);
		success = 1;
	}

	return success;
}

static int save_bmp_rgb888(FILE * hfile, int w, int h, void * pdata) {
	int success = 0;
	int size = 0;
	BMPHEADER head;
	BMPINFO info;

	size = get_rgb888_header(w, h, &head, &info);
	if (size > 0) {
		fwrite(head.bfType, 1, 14, hfile);
		fwrite(&info, 1, sizeof(info), hfile);
		fwrite(pdata, 1, size, hfile);
		success = 1;
	}

	return success;
}

static int save_bmp_rgb8888(FILE * hfile, int w, int h, void * pdata) {
	int success = 0;
	int size = 0;
	BMPHEADER head;
	BMPINFO info;

	size = get_rgb8888_header(w, h, &head, &info);

	if (size > 0) {
		fwrite(head.bfType, 1, 14, hfile);
		fwrite(&info, 1, sizeof(info), hfile);
		LOGI("HERE%d",size);
		fwrite(pdata, 1, size, hfile);

		success = 1;
	}
	return success;
}
JNIEXPORT jint JNICALL Java_com_shareScreen_CODE_decode
(JNIEnv * env, jobject thiz, jint width, jint height){
	int i=ffmepg_decorder(width,height);
	return i;

}

int yuv_to_rgb(int w,int h,AVFrame	*pFrame){
	LOGI("YUV TO RGBA");
		//FILE *src_file = fopen("/sdcard/hulei.yuv", "rb");
		int src_w = w, src_h = h;
		enum AVPixelFormat src_pixfmt = AV_PIX_FMT_YUV420P;
		int src_bpp = av_get_bits_per_pixel(av_pix_fmt_desc_get(src_pixfmt));

		FILE *dst_file = fopen("/sdcard/hulei.raw", "wb");
		int dst_w = w, dst_h = h;
		//enum AVPixelFormat dst_pixfmt = AV_PIX_FMT_RGB24;
		enum AVPixelFormat dst_pixfmt = AV_PIX_FMT_RGBA;
		int dst_bpp = av_get_bits_per_pixel(av_pix_fmt_desc_get(dst_pixfmt));

		//Structures
		uint8_t *src_data[4];
		int src_linesize[4];

		uint8_t *dst_data[4];
		int dst_linesize[4];

		int rescale_method = SWS_BICUBIC;
		struct SwsContext *img_convert_ctx;
		uint8_t *temp_buffer = (uint8_t *) malloc(src_w * src_h * src_bpp / 8);

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

		//av_opt_show2(img_convert_ctx, stdout, AV_OPT_FLAG_VIDEO_PARAM, NULL);
		//Set Value
		av_opt_set_int(img_convert_ctx,"sws_flags",SWS_BICUBIC | SWS_PRINT_INFO,NULL);
		av_opt_set_int(img_convert_ctx,"srcw",src_w,NULL);
		av_opt_set_int(img_convert_ctx,"srch",src_h,NULL);
		av_opt_set_int(img_convert_ctx,"src_format",src_pixfmt,NULL);
		//'0' for MPEG (Y:0-235);'1' for JPEG (Y:0-255)
		av_opt_set_int(img_convert_ctx, "src_range", 1, NULL);
		av_opt_set_int(img_convert_ctx, "dstw", dst_w, NULL);
		av_opt_set_int(img_convert_ctx, "dsth", dst_h, NULL);
		av_opt_set_int(img_convert_ctx, "dst_format", dst_pixfmt, NULL);
		av_opt_set_int(img_convert_ctx, "dst_range", 1, NULL);
		sws_init_context(img_convert_ctx, NULL, NULL);
		//Init Method 2
		//img_convert_ctx = sws_getContext(src_w, src_h,src_pixfmt, dst_w, dst_h, dst_pixfmt,
		ret=sws_setColorspaceDetails(img_convert_ctx,sws_getCoefficients(SWS_CS_ITU601),0,
				sws_getCoefficients(SWS_CS_ITU709),0,
				0, 1 << 16, 1 << 16);
		if (ret==-1) {
			printf( "Colorspace not support.\n");
			return -1;
		}
		//while (1)
		{
			/*if(fread(temp_buffer, 1, src_w * src_h * src_bpp / 8, src_file)!=src_w*src_h*src_bpp/8){
		 			LOGI("HHHHHH");
		 			break;
		 		}*/

			switch (src_pixfmt) {
			case AV_PIX_FMT_GRAY8: {
				memcpy(src_data[0], temp_buffer, src_w * src_h);
				break;
			}
			case AV_PIX_FMT_YUV420P: {
				/*memcpy(src_data[0], temp_buffer, src_w * src_h);                 //Y
		 			memcpy(src_data[1], temp_buffer + src_w * src_h, src_w * src_h / 4); //U
		 			memcpy(src_data[2], temp_buffer + src_w * src_h * 5 / 4,
		 					src_w * src_h / 4);  //V*/
				memcpy(src_data[0], pFrame->data[0], src_w * src_h);                 //Y
				memcpy(src_data[1], pFrame->data[1], src_w * src_h / 4); //U
				memcpy(src_data[2], pFrame->data[2], src_w * src_h / 4);
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
			default: {
				printf("Not Support Input Pixel Format.\n");
				break;
			}
			}

			sws_scale(img_convert_ctx, src_data, src_linesize, 0, src_h, dst_data,
					dst_linesize);
			LOGI("Finish process frame %5d\n", frame_idx);
			frame_idx++;

			switch (dst_pixfmt) {
			case AV_PIX_FMT_GRAY8: {
				fwrite(dst_data[0], 1, dst_w * dst_h, dst_file);
				break;
			}
			case AV_PIX_FMT_YUV420P: {
				LOGI("AV_PIX_FMT_YUV420P");
				fwrite(dst_data[0], 1, dst_w * dst_h, dst_file);                 //Y
				fwrite(dst_data[1], 1, dst_w * dst_h / 4, dst_file);             //U
				fwrite(dst_data[2], 1, dst_w * dst_h / 4, dst_file);             //V
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
				int size=fwrite(dst_data[0], 1, dst_w * dst_h * 3, dst_file);        //Packed
				LOGI("LENTH:%d",size);
				break;
			}
			case AV_PIX_FMT_RGBA: {
				//int size=fwrite(dst_data[0], 1, dst_w * dst_h * 4, dst_file);        //Packed
				//LOGI("save bmp");
				save_bmp1(dst_w,dst_h,dst_data[0]);

				break;
			}
			default: {
				printf("Not Support Output Pixel Format.\n");
				break;
			}
			}
		}
		sws_freeContext(img_convert_ctx);
		free(temp_buffer);
		fclose(dst_file);
		av_freep(&src_data[0]);
		av_freep(&dst_data[0]);
		return 0;
}
int save_bmp1(int w,int h,uint8_t *p)
{
	FILE* fb_out=NULL;
	const char* path_out="/sdcard/hulei.bmp";
	remove(path_out);
	fb_out=fopen(path_out,"wb");
	if(fb_out==NULL)
	{
		LOGI("open dst file failed");
		return -1;
	}
	void* pdst=NULL;
	pdst=p;
	int success=0;
	LOGI("SAVE RGBA BITMAP");
	success = save_bmp_rgb8888(fb_out, w, h, pdst);
	if(success==1)
	{
		LOGI("SUCCSESSED SAVE");
	}
	fclose(fb_out);
}
int ffmepg_decorder(int w,int h)
{
	AVFormatContext	*pFormatCtx;
	int				i, videoindex;
	AVCodecContext	*pCodecCtx;
	AVCodec			*pCodec;
	AVFrame	*pFrame,*pFrameYUV;
	uint8_t *out_buffer;
	AVPacket *packet;
	int y_size;
	int ret, got_picture;
	struct SwsContext *img_convert_ctx;

	char filepath[]="/sdcard/mypic.h264";
	//FILE *fp_yuv=fopen("/sdcard/hulei.yuv","wb+");
	av_register_all();
	avformat_network_init();
	pFormatCtx = avformat_alloc_context();
	int q=0;
	if((q=avformat_open_input(&pFormatCtx,filepath,NULL,NULL))!=0){
		LOGI("ERROR:%d",q);
		LOGI("Couldn't open input stream.\n");
		return -1;
	}
	if(avformat_find_stream_info(pFormatCtx,NULL)<0){
		LOGI("Couldn't find stream information.\n");
		return -1;
	}
	videoindex=-1;
	for(i=0; i<pFormatCtx->nb_streams; i++)
		if(pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO){
			videoindex=i;
			break;
		}

	if(videoindex==-1){
		LOGI("Didn't find a video stream.\n");
		return -1;
	}

	pCodecCtx=pFormatCtx->streams[videoindex]->codec;
	pCodec=avcodec_find_decoder(pCodecCtx->codec_id);
	if(pCodec==NULL){
		LOGI("Codec not found.\n");
		return -1;
	}
	if(avcodec_open2(pCodecCtx, pCodec,NULL)<0){
		LOGI("Could not open codec.\n");
		return -1;
	}

	pFrame=av_frame_alloc();
	pFrameYUV=av_frame_alloc();
	out_buffer=(uint8_t *)av_malloc(avpicture_get_size(PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height));
	LOGI("pCodecCtx->width:%d,pCodecCtx->height:%d",pCodecCtx->width,pCodecCtx->height);
	avpicture_fill((AVPicture *)pFrameYUV, out_buffer, PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height);
	packet=(AVPacket *)av_malloc(sizeof(AVPacket));
	//Output Info-----------------------------
	//printf("--------------- File Information ----------------\n");
	av_dump_format(pFormatCtx,0,filepath,0);
	printf("-------------------------------------------------\n");
	img_convert_ctx = sws_getContext(pCodecCtx->width, pCodecCtx->height, pCodecCtx->pix_fmt,
			pCodecCtx->width, pCodecCtx->height, PIX_FMT_YUV420P, SWS_BICUBIC, NULL, NULL, NULL);

	while(av_read_frame(pFormatCtx, packet)>=0){
		if(packet->stream_index==videoindex){
			ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_picture, packet);
			if(ret < 0){
				LOGI("Decode Error.");
				return -1;
			}


		}
		av_free_packet(packet);
	}
	//flush decoder
	//FIX: Flush Frames remained in Codec
	while (1) {
		ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_picture, packet);
		if (ret < 0)
			break;
		if (!got_picture)
			break;
		sws_scale(img_convert_ctx, (const uint8_t* const*)pFrame->data, pFrame->linesize, 0, pCodecCtx->height,
				pFrameYUV->data, pFrameYUV->linesize);

		int y_size=pCodecCtx->width*pCodecCtx->height;
		yuv_to_rgb(w,h,pFrameYUV);

		LOGI("Flush Decoder: Succeed to decode 1 frame!");
	}

	sws_freeContext(img_convert_ctx);

	//fclose(fp_yuv);

	av_frame_free(&pFrameYUV);
	av_frame_free(&pFrame);
	avcodec_close(pCodecCtx);
	avformat_close_input(&pFormatCtx);
	LOGI("end");
	return 0;
}
#endif//#ifndef WIN32
