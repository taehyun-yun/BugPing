<template>
  <div class="div">
    <div class="_2">
      <div class="rectangle-8"></div>
    </div>
    <div class="_1">
      <div class="rectangle-9"></div>
    </div>
    <div class="div2">
      <img class="right-arrow-1" src="right-arrow-10.png" alt="Arrow 1" />
      <img class="right-arrow-2" src="right-arrow-20.png" alt="Arrow 2" />
      <div class="_1-2-3-4-5">1 2 3 4 5</div>
    </div>
    <img class="div3" src="div1.svg" alt="Div Image" />
    <div class="div4">
      <div
        v-for="(date, index) in dates"
        :key="index"
        class="_2024-11-28-14-15"
      >
        {{ date }}
      </div>
    </div>
    <div class="div5">
      <div
        v-for="(count, index) in counts"
        :key="index"
        :class="`_${index + 12}`"
      >
        {{ count }}
      </div>
    </div>
    <div class="div6">
      <div
        v-for="(name, index) in names"
        :key="index"
        :class="`div${index + 7}`"
      >
        {{ name }}
      </div>
    </div>
    <div class="div13">
      <div
        v-for="(text, index) in texts"
        :key="index"
        :class="index === 0 ? 'div14' : `_${index + 16}`"
      >
        {{ text }}
      </div>
    </div>
    <div class="div17">
      <img
        v-for="(src, index) in imageSources"
        :key="index"
        :class="`_${index + 18}`"
        :src="src"
        :alt="'Image ' + (index + 1)"
      />
    </div>
    <div class="id">
      <div class="no">No.</div>
      <div v-for="(id, index) in ids" :key="index" :class="`_${id}`">
        {{ id }}
      </div>
    </div>
    <img class="div18" src="div16.svg" alt="Div 16" />
    <div class="div19">
      <div class="div20">
        <img class="polygon-1" src="polygon-10.svg" alt="Polygon" />
        <div class="div21">상태</div>
      </div>
      <div class="div22">작성 날짜</div>
      <div class="div23">보기권한</div>
      <div class="div24">작성자</div>
      <div class="div25">
        <div class="rectangle-57"></div>
        <div class="div26">삭제</div>
      </div>
      <div class="div27">타이틀</div>
    </div>
    <div class="div28">
      <div class="div29">
        <div class="div30">
          <div class="div31">+ 공지 작성하기</div>
        </div>
      </div>
      <div v-for="(item, index) in items" :key="index" class="div32">
        <div :class="`rectangle-65${index}`"></div>
        <div :class="`_${item.number}`">{{ item.number }}</div>
        <div :class="`div${item.index}`">{{ item.text }}</div>
        <img :src="item.src" :alt="item.text" :class="`div${item.index}`" />
      </div>
    </div>
    <div class="div39">
      <div class="group-5">
        <div class="div40">전체 게시물</div>
        <div class="rectangle-56"></div>
      </div>
      <div class="group-4">
        <div class="rectangle-55"></div>
        <div class="div41">내 게시물</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dates: [
        "2024.11.28(목) 14:15",
        "2024.11.28(목) 14:15",
        "2024.11.28(목) 14:15",
        "2024.11.28(목) 14:15",
        "2024.11.28(목) 14:15",
        "2024.11.28(목) 14:15",
      ],
      counts: ["1명", "1명", "2명", "1명", "1명", "1명"],
      names: ["코사", "문준호", "유지훈", "윤은선", "김태현", "장종현"],
      texts: [
        "집에 가고 싶어요",
        "오늘의 점심 메뉴는?",
        "테스트3",
        "테스트2",
        "테스트1",
        "공지사항",
      ],
      imageSources: [
        "_17.png",
        "_31.png",
        "_23.png",
        "_18.png",
        "_24.png",
        "_19.png",
      ],
      ids: [1, 2, 3, 4, 5, 6],
      items: [
        { number: 1, text: "특이사항", src: "div32.png", index: 33 },
        { number: 4, text: "매뉴얼", src: "div35.png", index: 35 },
        { number: 2, text: "공지", src: "div38.png", index: 38 },
      ],
    };
  },
};
</script>

<style scoped>
/* 여기에 style.css와 vars.css의 내용을 추가하세요 */
a,
button,
input,
select,
h1,
h2,
h3,
h4,
h5,
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  border: none;
  text-decoration: none;
  background: none;

  -webkit-font-smoothing: antialiased;
}

menu,
ol,
ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}
</style>
