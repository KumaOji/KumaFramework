"""Channels RPCs: GetChannelsStatus, RestartChannel."""

import asyncio
import logging

import grpc

from app.grpc.generated import kuma_agent_pb2

logger = logging.getLogger(__name__)


class ChannelsRouter:
    """Mixin: GetChannelsStatus, RestartChannel."""

    def GetChannelsStatus(self, request, context):
        try:
            from app.channels.service import get_channel_service

            service = get_channel_service()
            if service is None:
                return kuma_agent_pb2.ChannelsStatusResponse(
                    service_running=False,
                    channels={},
                )
            status = service.get_status()
            channels = {}
            for name, ch in status.get("channels", {}).items():
                channels[name] = kuma_agent_pb2.ChannelStatus(
                    enabled=ch.get("enabled", False),
                    running=ch.get("running", False),
                )
            return kuma_agent_pb2.ChannelsStatusResponse(
                service_running=status.get("service_running", False),
                channels=channels,
            )
        except Exception as exc:
            logger.exception("GetChannelsStatus failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def RestartChannel(self, request, context):
        try:
            from app.channels.service import get_channel_service

            service = get_channel_service()
            if service is None:
                context.abort(grpc.StatusCode.UNAVAILABLE, "Channel service is not running")
                return

            loop = asyncio.new_event_loop()
            try:
                success = loop.run_until_complete(service.restart_channel(request.name))
            finally:
                loop.close()

            if success:
                return kuma_agent_pb2.RestartChannelResponse(
                    success=True,
                    message=f"Channel {request.name} restarted successfully",
                )
            return kuma_agent_pb2.RestartChannelResponse(
                success=False,
                message=f"Failed to restart channel {request.name}",
            )
        except Exception as exc:
            logger.exception("RestartChannel failed for '%s'", request.name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))
