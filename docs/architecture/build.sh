#!/usr/bin/env bash

# This script builds the architecture documentation using Pandoc and Podman.
# The only requirement for this script to work is to have Podman installed on your system.
# If you desire to use docker instead of podman, you can replace the `podman` command with `docker`.
# Depending on your used docker version you might also have to remove the ":Z" after the volume mount.

podman build -t documentation-build-container:latest - << EOF
FROM pandoc/extra:3.2.0

RUN apk add openjdk21
RUN apk add graphviz
RUN apk add --no-cache fontconfig ttf-dejavu
RUN ln -s /usr/lib/libfontconfig.so.1 /usr/lib/libfontconfig.so && \
    ln -s /lib/libuuid.so.1 /usr/lib/libuuid.so.1 && \
    ln -s /lib/libc.musl-x86_64.so.1 /usr/lib/libc.musl-x86_64.so.1
ENV LD_LIBRARY_PATH=/usr/lib

RUN mkdir -p /root/.pandoc/plantuml && wget https://github.com/plantuml/plantuml/releases/download/v1.2024.6/plantuml-gplv2-1.2024.6.jar -O /root/.pandoc/plantuml/plantuml.jar

ENV PLANTUML=/root/.pandoc/plantuml/plantuml.jar

RUN tlmgr install pdflscape
EOF

podman run -it --rm --volume $PWD:/data:Z documentation-build-container:latest README.md \
    -o README.pdf \
    --template /root/.pandoc/templates/eisvogel.latex \
    --listings -V lang=en \
    --filter pandoc-crossref \
    --lua-filter /root/.pandoc/filters/diagram-generator.lua \
    -Mlistings
