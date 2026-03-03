(ns scicloj.lalinea.vis
  "Visualization helpers for linear algebra."
  (:require [scicloj.kindly.v4.kind :as kind]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]))

(defn arrow-plot
  "Render 2D vectors as arrows on an SVG grid.

  `arrows` is a sequence of maps, each with:
    :xy     — [dx dy] displacement vector (required)
    :color  — CSS color string (required)
    :label  — text label (optional)
    :from   — [fx fy] origin offset, default [0 0] (optional)
    :dashed? — dashed stroke (optional)

  `opts` is a map with:
    :width  — SVG width in pixels, default 300 (optional)"
  [arrows opts]
  (let [width (or (:width opts) 300)
        all-pts (mapcat (fn [{:keys [xy from]}]
                          (let [[fx fy] (or from [0 0])
                                [tx ty] [(+ fx (xy 0)) (+ fy (xy 1))]]
                            [[fx fy] [tx ty]]))
                        arrows)
        all-xs (map first all-pts)
        all-ys (map second all-pts)
        x-min (apply min all-xs)
        x-max (apply max all-xs)
        y-min (apply min all-ys)
        y-max (apply max all-ys)
        dx (- x-max x-min)
        dy (- y-max y-min)
        span (max dx dy 1.0)
        pad (* 0.3 span)
        vb-x (- x-min pad)
        vb-w (+ dx (* 2 pad))
        vb-h (+ dy (* 2 pad))
        vb-y-top (+ y-max pad)
        height (* width (/ vb-h vb-w))
        px-per-unit (/ width vb-w)
        stroke-w (/ 2.0 px-per-unit)
        head-w (* 10 stroke-w)
        head-h (* 7 stroke-w)
        font-size (* 12 stroke-w)
        grid-lo-x (long (Math/floor (- x-min pad)))
        grid-hi-x (long (Math/ceil (+ x-max pad)))
        grid-lo-y (long (Math/floor (- y-min pad)))
        grid-hi-y (long (Math/ceil (+ y-max pad)))
        grid-lines (concat
                    (for [gx (range grid-lo-x (inc grid-hi-x))]
                      [:line {:x1 gx :y1 (- grid-lo-y) :x2 gx :y2 (- grid-hi-y)
                              :stroke (if (zero? gx) "#999" "#ddd")
                              :stroke-width (if (zero? gx) stroke-w (* 0.5 stroke-w))}])
                    (for [gy (range grid-lo-y (inc grid-hi-y))]
                      [:line {:x1 grid-lo-x :y1 (- gy) :x2 grid-hi-x :y2 (- gy)
                              :stroke (if (zero? gy) "#999" "#ddd")
                              :stroke-width (if (zero? gy) stroke-w (* 0.5 stroke-w))}]))
        defs (into [:defs]
                   (map (fn [{:keys [color]}]
                          [:marker {:id (str "ah-" (subs color 1))
                                    :markerWidth head-w :markerHeight head-h
                                    :refX head-w :refY (/ head-h 2)
                                    :orient "auto" :markerUnits "userSpaceOnUse"}
                           [:polygon {:points (str "0 0, " head-w " " (/ head-h 2) ", 0 " head-h)
                                      :fill color}]])
                        arrows))
        arrow-elts (mapcat
                    (fn [{:keys [label xy color from dashed?]}]
                      (let [[fx fy] (or from [0 0])
                            [tx ty] [(+ fx (xy 0)) (+ fy (xy 1))]
                            adx (- tx fx) ady (- ty fy)
                            len (Math/sqrt (+ (* adx adx) (* ady ady)))
                            nx (if (pos? len) (/ (- ady) len) 0)
                            ny (if (pos? len) (/ adx len) 0)
                            lx (+ fx (* 0.7 adx) (* font-size 0.7 nx))
                            ly (+ fy (* 0.7 ady) (* font-size 0.7 ny))]
                        (cond-> [[:line (cond-> {:x1 fx :y1 (- fy) :x2 tx :y2 (- ty)
                                                 :stroke color :stroke-width (* 1.5 stroke-w)
                                                 :marker-end (str "url(#ah-" (subs color 1) ")")}
                                          dashed? (assoc :stroke-dasharray (str (* 4 stroke-w) " " (* 3 stroke-w))))]]
                          label (conj [:text {:x lx :y (- ly)
                                              :fill color
                                              :font-size font-size
                                              :font-family "sans-serif"
                                              :font-weight "bold"
                                              :text-anchor "middle"
                                              :dominant-baseline "central"}
                                       label]))))
                    arrows)]
    (kind/hiccup
     (into [:svg {:width width :height height
                  :viewBox (str vb-x " " (- vb-y-top) " " vb-w " " vb-h)}]
           (concat [defs]
                   grid-lines
                   arrow-elts)))))

(defn graph-plot
  "Render a graph as an SVG diagram with nodes and edges.

  `positions` is a vector of [x y] coordinates for each node.
  `edges` is a sequence of [i j] index pairs.
  `opts` is a map with:
    :width          — SVG width in pixels, default 300 (optional)
    :labels         — vector of node labels, default [\"0\" \"1\" ...] (optional)
    :node-colors    — vector of CSS colors per node (optional)
    :edge-highlight — set of [i j] pairs to highlight in red (optional)"
  [positions edges opts]
  (let [width (or (:width opts) 300)
        n (count positions)
        labels (or (:labels opts) (mapv str (range n)))
        node-colors (or (:node-colors opts) (vec (repeat n "#2266cc")))
        edge-hl (or (:edge-highlight opts) #{})
        xs (mapv first positions)
        ys (mapv second positions)
        x-min (apply min xs) x-max (apply max xs)
        y-min (apply min ys) y-max (apply max ys)
        dx (- x-max x-min) dy (- y-max y-min)
        span (max dx dy 1.0)
        pad (* 0.4 span)
        vb-x (- x-min pad) vb-w (+ dx (* 2 pad))
        vb-h (+ dy (* 2 pad))
        vb-y-top (+ y-max pad)
        height (* width (/ vb-h vb-w))
        px-per-unit (/ width vb-w)
        r (* 0.22 span (min 1.0 (/ 3.0 (max n 1))))
        stroke-w (/ 1.5 px-per-unit)
        font-size (* 0.7 r)
        edge-elts (mapv (fn [[i j]]
                          (let [[x1 y1] (positions i)
                                [x2 y2] (positions j)
                                hl? (or (edge-hl [i j]) (edge-hl [j i]))]
                            [:line {:x1 x1 :y1 (- y1) :x2 x2 :y2 (- y2)
                                    :stroke (if hl? "#cc4422" "#999")
                                    :stroke-width (if hl? (* 3 stroke-w) (* 1.5 stroke-w))}]))
                        edges)
        node-elts (mapcat (fn [i]
                            (let [[cx cy] (positions i)]
                              [[:circle {:cx cx :cy (- cy) :r r
                                         :fill (node-colors i)
                                         :stroke "#333" :stroke-width stroke-w}]
                               [:text {:x cx :y (- cy)
                                       :fill "white"
                                       :font-size font-size
                                       :font-family "sans-serif"
                                       :font-weight "bold"
                                       :text-anchor "middle"
                                       :dominant-baseline "central"}
                                (labels i)]]))
                          (range n))]
    (kind/hiccup
     (into [:svg {:width width :height height
                  :viewBox (str vb-x " " (- vb-y-top) " " vb-w " " vb-h)}]
           (concat edge-elts node-elts)))))

(defn matrix->gray-image
  "Convert a numeric `[h w]` matrix to a `[h w 3]` uint8 grayscale tensor
  suitable for display as an image. Values are clamped to [0, 255]."
  [m]
  (let [[h w] (dtype/shape m)]
    (dtt/compute-tensor [h w 3]
                           (fn [r c _ch]
                             (int (max 0 (min 255 (dtt/mget m r c)))))
                           :uint8)))

(defn extract-channel
  "Extract a single channel from an `[h w c]` image tensor and return
  it as a `[h w 3]` grayscale tensor (the channel replicated three times)."
  [img ch]
  (let [[h w _c] (dtype/shape img)
        channel (dtt/select img :all :all ch)]
    (dtt/compute-tensor [h w 3]
                           (fn [r c _] (int (dtt/mget channel r c)))
                           :uint8)))
